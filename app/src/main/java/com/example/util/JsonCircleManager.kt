package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.model.CircleMember
import com.example.model.MemberPaymentStatus
import com.example.model.MoneyCircle
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

object JsonCircleManager {

  /**
   * Serializes a circle and its members to a clean, portable JSON string
   */
  fun exportCircleToJson(circle: MoneyCircle, members: List<CircleMember>): String {
    val root = JSONObject()
    root.put("version", 1)
    root.put("app", "Hassala")
    root.put("timestamp", System.currentTimeMillis())

    val circleObj = JSONObject()
    circleObj.put("id", circle.id)
    circleObj.put("titleEn", circle.titleEn)
    circleObj.put("titleAr", circle.titleAr)
    circleObj.put("categoryEn", circle.categoryEn)
    circleObj.put("categoryAr", circle.categoryAr)
    circleObj.put("totalPoolAmount", circle.totalPoolAmount)
    circleObj.put("monthlyContribution", circle.monthlyContribution)
    circleObj.put("currencySymbol", circle.currencySymbol)
    circleObj.put("totalMonths", circle.totalMonths)
    circleObj.put("currentCycleMonth", circle.currentCycleMonth)
    circleObj.put("nextPayoutDateEn", circle.nextPayoutDateEn)
    circleObj.put("nextPayoutDateAr", circle.nextPayoutDateAr)
    circleObj.put("daysRemaining", circle.daysRemaining)
    circleObj.put("userTurn", circle.userTurn)
    circleObj.put("escrowGuaranteeId", circle.escrowGuaranteeId)
    root.put("circle", circleObj)

    val membersArr = JSONArray()
    for (m in members) {
      val mObj = JSONObject()
      mObj.put("id", m.id)
      mObj.put("nameEn", m.nameEn)
      mObj.put("nameAr", m.nameAr)
      mObj.put("roleEn", m.roleEn)
      mObj.put("roleAr", m.roleAr)
      mObj.put("turnNumber", m.turnNumber)
      mObj.put("payoutMonthEn", m.payoutMonthEn)
      mObj.put("payoutMonthAr", m.payoutMonthAr)
      mObj.put("monthlyContribution", m.monthlyContribution)
      mObj.put("isPaid", m.status == MemberPaymentStatus.PAID)
      mObj.put("isCurrentUser", m.isCurrentUser)
      mObj.put("avatarInitials", m.avatarInitials)
      mObj.put("avatarColorHex", m.avatarColorHex)
      mObj.put("phone", m.phone)
      mObj.put("paidTimestampEn", m.paidTimestampEn ?: "")
      mObj.put("paidTimestampAr", m.paidTimestampAr ?: "")
      membersArr.put(mObj)
    }
    root.put("members", membersArr)

    return root.toString(2)
  }

  /**
   * Writes the JSON to a file and launches the Android Share Sheet
   */
  fun shareCircleJsonFile(context: Context, circle: MoneyCircle, members: List<CircleMember>) {
    try {
      val jsonString = exportCircleToJson(circle, members)
      val file = File(context.cacheDir, "hassala_circle_${circle.id}.json")
      FileOutputStream(file).use { out ->
        out.write(jsonString.toByteArray(Charsets.UTF_8))
      }

      val contentUri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
      )

      val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/json"
        putExtra(Intent.EXTRA_STREAM, contentUri)
        putExtra(Intent.EXTRA_SUBJECT, "Hassala Circle Backup - ${circle.titleAr}")
        putExtra(Intent.EXTRA_TEXT, "ملف مشاركة حلقة ادخار (حصّالة): ${circle.titleAr}\nيمكنك استيراد هذا الملف مباشرة داخل تطبيق حصّالة.")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }

      context.startActivity(Intent.createChooser(sendIntent, "مشاركة ملف الجمعية (JSON)"))
    } catch (e: Exception) {
      // Fallback: share JSON text directly
      val jsonString = exportCircleToJson(circle, members)
      val textIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, jsonString)
      }
      context.startActivity(Intent.createChooser(textIntent, "مشاركة بيانات الجمعية (JSON)"))
    }
  }

  /**
   * Parses JSON string into MoneyCircle and List<CircleMember>
   */
  fun parseCircleFromJson(jsonString: String): Pair<MoneyCircle, List<CircleMember>>? {
    return try {
      val root = JSONObject(jsonString)
      val circleObj = root.getJSONObject("circle")
      val circle = MoneyCircle(
        id = circleObj.optString("id", "circle_imported_${System.currentTimeMillis()}"),
        titleEn = circleObj.optString("titleEn", "Custom Money Circle"),
        titleAr = circleObj.optString("titleAr", "جمعية مخصصة"),
        categoryEn = circleObj.optString("categoryEn", "Savings Pool"),
        categoryAr = circleObj.optString("categoryAr", "حلقة ادخار"),
        totalPoolAmount = circleObj.optLong("totalPoolAmount", 0L),
        monthlyContribution = circleObj.optLong("monthlyContribution", 1000L),
        currencySymbol = circleObj.optString("currencySymbol", "ر.س"),
        totalMonths = circleObj.optInt("totalMonths", 10),
        currentCycleMonth = circleObj.optInt("currentCycleMonth", 1),
        nextPayoutDateEn = circleObj.optString("nextPayoutDateEn", "Next Month"),
        nextPayoutDateAr = circleObj.optString("nextPayoutDateAr", "الشهر القادم"),
        daysRemaining = circleObj.optInt("daysRemaining", 30),
        userTurn = circleObj.optInt("userTurn", 1),
        escrowGuaranteeId = circleObj.optString("escrowGuaranteeId", "HSSL-IMPORT-${System.currentTimeMillis() % 10000}"),
        shariaCertified = true
      )

      val membersArr = root.optJSONArray("members") ?: JSONArray()
      val membersList = mutableListOf<CircleMember>()
      for (i in 0 until membersArr.length()) {
        val m = membersArr.getJSONObject(i)
        membersList.add(
          CircleMember(
            id = m.optString("id", "m_$i"),
            nameEn = m.optString("nameEn", "Member $i"),
            nameAr = m.optString("nameAr", "عضو $i"),
            roleEn = m.optString("roleEn", "Member"),
            roleAr = m.optString("roleAr", "عضو"),
            turnNumber = m.optInt("turnNumber", i + 1),
            payoutMonthEn = m.optString("payoutMonthEn", "Month ${i + 1}"),
            payoutMonthAr = m.optString("payoutMonthAr", "الجولة ${i + 1}"),
            monthlyContribution = m.optLong("monthlyContribution", circle.monthlyContribution),
            status = if (m.optBoolean("isPaid", false)) MemberPaymentStatus.PAID else MemberPaymentStatus.PENDING,
            isCurrentUser = m.optBoolean("isCurrentUser", false),
            avatarInitials = m.optString("avatarInitials", "M"),
            avatarColorHex = m.optLong("avatarColorHex", 0xFF047857),
            phone = m.optString("phone", ""),
            paidTimestampEn = m.optString("paidTimestampEn").ifEmpty { null },
            paidTimestampAr = m.optString("paidTimestampAr").ifEmpty { null }
          )
        )
      }

      Pair(circle, membersList)
    } catch (e: Exception) {
      null
    }
  }
}
