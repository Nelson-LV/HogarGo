package com.hogargo.app.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.automirrored.outlined.DirectionsBike
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.SportsBasketball
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.ui.graphics.Color
import com.hogargo.app.R

val FamilyMembers = listOf(
    FamilyMember("mom", R.string.member_mom, R.drawable.avatar_mama),
    FamilyMember("dad", R.string.member_dad, R.drawable.avatar_papa),
    FamilyMember("kid", R.string.member_kid, R.drawable.avatar_leo),
)

fun familyMember(id: String?): FamilyMember? = FamilyMembers.find { it.id == id }

val InitialTasks = listOf(
    HouseTask(
        id = "empty_dishwasher",
        titleRes = R.string.task_empty_dishwasher,
        category = TaskCategory.KITCHEN,
        dueTime = "5 PM",
        completed = false,
        assigneeId = "mom",
        coinReward = 15,
    ),
    HouseTask(
        id = "water_tomatoes",
        titleRes = R.string.task_water_tomatoes,
        category = TaskCategory.GARDEN,
        completed = true,
        assigneeId = "mom",
        coinReward = 20,
    ),
    HouseTask(
        id = "fold_laundry",
        titleRes = R.string.task_fold_laundry,
        category = TaskCategory.GENERAL,
        completed = false,
        assigneeId = null,
        coinReward = 30,
    ),
)

val NextTask = HouseTask(
    id = "wash_dishes",
    titleRes = R.string.task_wash_dishes,
    category = TaskCategory.KITCHEN,
    minutes = 15,
    completed = false,
    coinReward = 10,
)

val UpcomingChores = listOf(
    UpcomingChore(R.string.task_vacuum_living_room, WhenLabel.TODAY_4PM),
    UpcomingChore(R.string.task_water_plants, WhenLabel.TOMORROW),
)

val HomeSavingsGoal = SavingsGoal(R.string.home_savings_goal, current = 650, target = 1000)
val FinanceSavingsGoal = SavingsGoal(R.string.savings_goal_disneyland, current = 1300, target = 2000)

const val MonthlySpent = 3420
const val MonthlyRemaining = 1080

val ExpenseBreakdown = listOf(
    ExpenseCategoryShare(R.string.expense_category_groceries, 850.0, 0.45f, Color(0xFF8D4F11)),
    ExpenseCategoryShare(R.string.expense_category_bills, 420.0, 0.25f, Color(0xFF904917)),
    ExpenseCategoryShare(R.string.expense_category_pet, 150.0, 0.10f, Color(0xFFFEAC67)),
    ExpenseCategoryShare(R.string.expense_category_leisure, 300.0, 0.20f, Color(0xFFFFDBC9)),
)

val RecentExpenses = listOf(
    Expense(
        id = "trader_joes",
        merchantRes = R.string.expense_trader_joes,
        categoryRes = R.string.expense_category_groceries,
        amount = 142.50,
        dateRes = R.string.date_yesterday,
        icon = Icons.Outlined.ShoppingCart,
        iconBackground = Color(0xFFFEAC67),
    ),
    Expense(
        id = "vet",
        merchantRes = R.string.expense_vet,
        categoryRes = R.string.expense_category_pet,
        amount = 85.00,
        dateRes = R.string.date_mon_12,
        icon = Icons.Outlined.Pets,
        iconBackground = Color(0xFFFFDBC9),
    ),
    Expense(
        id = "electric",
        merchantRes = R.string.expense_electric,
        categoryRes = R.string.expense_category_services,
        amount = 120.00,
        dateRes = R.string.date_sun_11,
        icon = Icons.Outlined.Bolt,
        iconBackground = Color(0xFFBB5808),
    ),
)

val UpcomingBills = listOf(
    Bill(
        id = "electricity",
        nameRes = R.string.bill_electricity,
        amount = 124.50,
        dueToday = true,
        icon = Icons.Outlined.Bolt,
        iconBackground = Color(0xFFFFDAD6),
    ),
    Bill(
        id = "internet",
        nameRes = R.string.bill_internet,
        amount = 79.99,
        dueToday = false,
        dateLabel = "Oct 20",
        icon = Icons.Outlined.Wifi,
        iconBackground = Color(0xFFFEAC67),
    ),
)

val CalendarDots = listOf(
    CalendarDayInfo(1, listOf(Color(0xFFFFDAD6))),
    CalendarDayInfo(3, listOf(Color(0xFFFEAC67))),
    CalendarDayInfo(8, listOf(Color(0xFFFEAC67))),
    CalendarDayInfo(12, listOf(Color(0xFFFEAC67))),
    CalendarDayInfo(15, listOf(Color(0xFFFEAC67), Color(0xFFFFDAD6))),
    CalendarDayInfo(22, listOf(Color(0xFFFEAC67))),
    CalendarDayInfo(28, listOf(Color(0xFFFEAC67))),
    CalendarDayInfo(30, listOf(Color(0xFFFFDAD6))),
)
const val CalendarSelectedDay = 15
const val CalendarYear = 2023
const val CalendarMonthIndex = 9 // October, 0-based

val InitialPetState = PetState(level = 12, happiness = 0.85f, satiety = 0.60f)

val WardrobeItems = listOf(
    PetWardrobeItem("scarf", R.string.pet_item_scarf, Icons.Outlined.Checkroom, unlocked = true),
    PetWardrobeItem("glasses", R.string.pet_item_glasses, Icons.Outlined.Visibility, unlocked = false, tasksRemaining = 3),
    PetWardrobeItem("ball", R.string.pet_item_ball, Icons.Outlined.SportsBasketball, unlocked = true),
    PetWardrobeItem("skateboard", R.string.pet_item_skateboard, Icons.AutoMirrored.Outlined.DirectionsBike, unlocked = false, tasksRemaining = 10),
)
