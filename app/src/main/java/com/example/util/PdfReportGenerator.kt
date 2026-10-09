package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.model.CircleMember
import com.example.model.MemberPaymentStatus
import com.example.model.MoneyCircle
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportGenerator {

  /**
   * Generates a high-quality A4 PDF summary document of the money circle and shares it
   */
  fun generateAndSharePdfReport(
    context: Context,
    circle: MoneyCircle,
    members: List<CircleMember>
  ) {
    val pdfDocument = PdfDocument()
    val pageWidth = 595
    val pageHeight = 842

    val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas: Canvas = page.canvas

    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    val formattedDate = SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale.ENGLISH).format(Date())

    val totalMembers = members.size
    val paidMembers = members.count { it.status == MemberPaymentStatus.PAID }
    val collectedAmount = paidMembers * circle.monthlyContribution
    val pendingAmount = (totalMembers - paidMembers) * circle.monthlyContribution
    val percent = if (totalMembers > 0) (paidMembers * 100) / totalMembers else 0

    // Paints
    val bgPaint = Paint().apply { color = Color.WHITE }
    val emeraldHeaderPaint = Paint().apply { color = Color.rgb(6, 78, 59) } // #064E3B
    val goldAccentPaint = Paint().apply { color = Color.rgb(212, 175, 55) } // #D4AF37
    val cardBgPaint = Paint().apply { color = Color.rgb(248, 250, 252) }
    val cardBorderPaint = Paint().apply {
      color = Color.rgb(226, 232, 240)
      style = Paint.Style.STROKE
      strokeWidth = 1f
    }

    val textWhiteTitle = Paint().apply {
      color = Color.WHITE
      textSize = 20f
      isFakeBoldText = true
      isAntiAlias = true
    }

    val textWhiteSub = Paint().apply {
      color = Color.rgb(203, 213, 225)
      textSize = 10f
      isAntiAlias = true
    }

    val textDarkHeader = Paint().apply {
      color = Color.rgb(15, 23, 42)
      textSize = 14f
      isFakeBoldText = true
      isAntiAlias = true
    }

    val textDarkBody = Paint().apply {
      color = Color.rgb(51, 65, 85)
      textSize = 10f
      isAntiAlias = true
    }

    val textMuted = Paint().apply {
      color = Color.rgb(100, 116, 139)
      textSize = 9f
      isAntiAlias = true
    }

    val textEmeraldBold = Paint().apply {
      color = Color.rgb(4, 120, 87)
      textSize = 16f
      isFakeBoldText = true
      isAntiAlias = true
    }

    val textGoldBold = Paint().apply {
      color = Color.rgb(180, 83, 9)
      textSize = 16f
      isFakeBoldText = true
      isAntiAlias = true
    }

    // 1. Draw Page Background
    canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), bgPaint)

    // 2. Header Banner
    canvas.drawRect(0f, 0f, pageWidth.toFloat(), 95f, emeraldHeaderPaint)
    canvas.drawRect(0f, 92f, pageWidth.toFloat(), 95f, goldAccentPaint)

    canvas.drawText("تقرير ملخص الجمعية المالي الموحد - حصّالة", 30f, 40f, textWhiteTitle)
    canvas.drawText("Hassala Digital ROSCA • Official Ledger & Summary Sheet", 30f, 58f, textWhiteSub)
    canvas.drawText("تاريخ الإصدار: $formattedDate  |  رقم الضمان: ${circle.escrowGuaranteeId}", 30f, 76f, textWhiteSub)

    // 3. Circle Info Card
    val infoCard = RectF(30f, 110f, (pageWidth - 30).toFloat(), 180f)
    canvas.drawRoundRect(infoCard, 12f, 12f, cardBgPaint)
    canvas.drawRoundRect(infoCard, 12f, 12f, cardBorderPaint)

    canvas.drawText("بيانات الجمعية: ${circle.titleAr} (${circle.titleEn})", 45f, 132f, textDarkHeader)
    canvas.drawText("الفئة: ${circle.categoryAr}  |  مدة الجمعية: ${circle.totalMonths} أشهر  |  الجولة الحالية: رقم ${circle.currentCycleMonth}", 45f, 150f, textDarkBody)
    canvas.drawText("العملة: ${circle.currencySymbol}  |  الصرف القادم: ${circle.nextPayoutDateAr}  |  المتبقي: ${circle.daysRemaining} يوم", 45f, 166f, textMuted)

    // 4. Financial Metric Boxes (4 columns)
    val boxWidth = (pageWidth - 60 - 30) / 4f
    val boxTop = 195f
    val boxBottom = 260f

    // Box 1: Total Pool
    drawMetricBox(canvas, 30f, boxTop, boxWidth, boxBottom, "إجمالي الصندوق", "${circle.currencySymbol}${numberFormat.format(circle.totalPoolAmount)}", cardBgPaint, cardBorderPaint, textMuted, textEmeraldBold)

    // Box 2: Monthly Dues
    drawMetricBox(canvas, 30f + boxWidth + 10, boxTop, boxWidth, boxBottom, "القسط الشهري", "${circle.currencySymbol}${numberFormat.format(circle.monthlyContribution)}", cardBgPaint, cardBorderPaint, textMuted, textDarkHeader)

    // Box 3: Collected
    drawMetricBox(canvas, 30f + (boxWidth + 10) * 2, boxTop, boxWidth, boxBottom, "المحصل هذا الشهر", "${circle.currencySymbol}${numberFormat.format(collectedAmount)} ($percent%)", cardBgPaint, cardBorderPaint, textMuted, textEmeraldBold)

    // Box 4: Pending
    drawMetricBox(canvas, 30f + (boxWidth + 10) * 3, boxTop, boxWidth, boxBottom, "المبلغ المعلق", "${circle.currencySymbol}${numberFormat.format(pendingAmount)}", cardBgPaint, cardBorderPaint, textMuted, textGoldBold)

    // 5. Member Roster Table
    val tableTop = 280f
    canvas.drawText("كشف حالة الأعضاء والأدوار (${members.size} عضو)", 30f, tableTop, textDarkHeader)

    // Table Header
    val thTop = tableTop + 10f
    val thBottom = thTop + 24f
    val thRect = RectF(30f, thTop, (pageWidth - 30).toFloat(), thBottom)
    canvas.drawRoundRect(thRect, 6f, 6f, emeraldHeaderPaint)

    val thText = Paint().apply {
      color = Color.WHITE
      textSize = 10f
      isFakeBoldText = true
      isAntiAlias = true
    }

    canvas.drawText("الدور", 42f, thTop + 16f, thText)
    canvas.drawText("اسم العضو", 85f, thTop + 16f, thText)
    canvas.drawText("رقم الهاتف", 230f, thTop + 16f, thText)
    canvas.drawText("شهر الاستحقاق", 340f, thTop + 16f, thText)
    canvas.drawText("القسط", 435f, thTop + 16f, thText)
    canvas.drawText("الحالة", 500f, thTop + 16f, thText)

    // Table Rows
    var rowY = thBottom + 6f
    val rowHeight = 26f
    val rowBgEven = Paint().apply { color = Color.rgb(255, 255, 255) }
    val rowBgOdd = Paint().apply { color = Color.rgb(241, 245, 249) }

    val statusPaidPaint = Paint().apply {
      color = Color.rgb(4, 120, 87)
      isFakeBoldText = true
      textSize = 9.5f
      isAntiAlias = true
    }

    val statusPendingPaint = Paint().apply {
      color = Color.rgb(180, 83, 9)
      isFakeBoldText = true
      textSize = 9.5f
      isAntiAlias = true
    }

    if (members.isEmpty()) {
      val rowRect = RectF(30f, rowY, (pageWidth - 30).toFloat(), rowY + rowHeight)
      canvas.drawRect(rowRect, rowBgEven)
      canvas.drawText("لا يوجد أعضاء مسجلين في الجمعية بعد - بانتظار إضافة المشتركين", 45f, rowY + 17f, textMuted)
    } else {
      for ((index, member) in members.take(18).withIndex()) {
        val rowRect = RectF(30f, rowY, (pageWidth - 30).toFloat(), rowY + rowHeight)
        canvas.drawRect(rowRect, if (index % 2 == 0) rowBgOdd else rowBgEven)

        canvas.drawText("#${member.turnNumber}", 42f, rowY + 17f, textDarkHeader)
        canvas.drawText(member.nameAr.ifEmpty { member.nameEn }, 85f, rowY + 17f, textDarkBody)
        canvas.drawText(member.phone.ifEmpty { "-" }, 230f, rowY + 17f, textMuted)
        canvas.drawText(member.payoutMonthAr, 340f, rowY + 17f, textDarkBody)
        canvas.drawText("${circle.currencySymbol}${member.monthlyContribution}", 435f, rowY + 17f, textDarkBody)

        if (member.status == MemberPaymentStatus.PAID) {
          canvas.drawText("مدفوع ✓", 500f, rowY + 17f, statusPaidPaint)
        } else {
          canvas.drawText("معلق ⏳", 500f, rowY + 17f, statusPendingPaint)
        }

        rowY += rowHeight
      }
    }

    // 6. Footer & Escrow Guarantee Note
    val footerTop = pageHeight - 65f
    canvas.drawLine(30f, footerTop, (pageWidth - 30).toFloat(), footerTop, cardBorderPaint)

    canvas.drawText("✓ هذا التقرير صادر وموثق آلياً من تطبيق حصّالة (Hassala) المتوافق مع معايير الجمعيات المالية التكافلية وحسابات الضمان المصرفي.", 30f, footerTop + 20f, textMuted)
    canvas.drawText("جميع الحقوق محفوظة © حصّالة للتقنية المالية • لا يمثل هذا المستند سند مديونية ربوي • وثيقة تنظيمية تكافلية رقمية.", 30f, footerTop + 35f, textMuted)

    pdfDocument.finishPage(page)

    // Save PDF to Cache and Trigger Intent
    val pdfFile = File(context.cacheDir, "hassala_summary_${circle.id}.pdf")
    FileOutputStream(pdfFile).use { out ->
      pdfDocument.writeTo(out)
    }
    pdfDocument.close()

    val fileUri = FileProvider.getUriForFile(
      context,
      "${context.packageName}.fileprovider",
      pdfFile
    )

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
      type = "application/pdf"
      putExtra(Intent.EXTRA_STREAM, fileUri)
      putExtra(Intent.EXTRA_SUBJECT, "ملخص تقرير جمعية حصّالة - ${circle.titleAr}")
      putExtra(Intent.EXTRA_TEXT, "مرفق تقرير ملخص الجمعية المالي الرسمي بصيغة PDF لتطبيق حصّالة (Hassala).")
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(sendIntent, "مشاركة تقرير الجمعية (PDF)"))
  }

  private fun drawMetricBox(
    canvas: Canvas,
    left: Float,
    top: Float,
    width: Float,
    bottom: Float,
    label: String,
    value: String,
    bgPaint: Paint,
    borderPaint: Paint,
    labelPaint: Paint,
    valPaint: Paint
  ) {
    val rect = RectF(left, top, left + width, bottom)
    canvas.drawRoundRect(rect, 8f, 8f, bgPaint)
    canvas.drawRoundRect(rect, 8f, 8f, borderPaint)

    canvas.drawText(label, left + 10f, top + 22f, labelPaint)
    canvas.drawText(value, left + 10f, top + 48f, valPaint)
  }
}
