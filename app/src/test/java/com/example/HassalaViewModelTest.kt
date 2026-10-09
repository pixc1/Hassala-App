package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.model.ActiveTab
import com.example.model.AppLanguage
import com.example.viewmodel.HassalaViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HassalaViewModelTest {

  private lateinit var viewModel: HassalaViewModel

  @Before
  fun setUp() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    viewModel = HassalaViewModel(app)
  }

  @Test
  fun testInitialState() {
    val state = viewModel.uiState.value
    assertEquals(0L, state.circle.totalPoolAmount)
    assertEquals(1000L, state.circle.monthlyContribution)
    assertEquals(10, state.circle.totalMonths)
    assertEquals("ر.س", state.circle.currencySymbol)
    assertEquals(ActiveTab.DASHBOARD, state.activeTab)
    assertEquals(AppLanguage.ARABIC, state.language)
    assertEquals(0, state.members.size)
  }

  @Test
  fun testToggleLanguage() {
    viewModel.toggleLanguage()
    assertEquals(AppLanguage.ENGLISH, viewModel.uiState.value.language)
    viewModel.toggleLanguage()
    assertEquals(AppLanguage.ARABIC, viewModel.uiState.value.language)
  }

  @Test
  fun testUpdateCircleDetails() {
    viewModel.updateCircleDetails(
      titleAr = "جمعية النخبة",
      titleEn = "Elite Circle",
      monthlyContribution = 2000L,
      totalMonths = 12,
      currencySymbol = "د.إ",
      currentMonth = 1
    )
    assertNotNull(viewModel.uiState.value)
  }

  @Test
  fun testPayCurrentUserDues() {
    viewModel.payCurrentUserDues("Mada Card")
    assertNotNull(viewModel.uiState.value)
  }

  @Test
  fun testAdvanceCycleMonth() {
    viewModel.advanceCycleMonth()
    assertNotNull(viewModel.uiState.value)
  }
}
