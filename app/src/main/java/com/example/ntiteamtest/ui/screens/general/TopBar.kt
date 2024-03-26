package com.example.ntiteamtest.ui.screens.general

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyRuble
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.ntiteamtest.R
import com.example.ntiteamtest.data.models.Categories
import com.example.ntiteamtest.data.models.Tags
import com.example.ntiteamtest.ui.navigation.NavRoutes
import com.example.ntiteamtest.ui.screens.utilScreens.ScreenError
import com.example.ntiteamtest.ui.screens.utilScreens.ScreenLoading
import com.example.ntiteamtest.ui.states.UiState
import com.skydoves.flexible.bottomsheet.material3.FlexibleBottomSheet
import com.skydoves.flexible.core.FlexibleSheetSize
import com.skydoves.flexible.core.rememberFlexibleBottomSheetState
import kotlinx.coroutines.launch
import org.junit.experimental.categories.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navigator: NavHostController,
    tags: State<List<Tags>>,
    filterTagList: MutableList<Int>,
    onFilter: (List<Int>) -> Unit,
    cartProductsAmount: Int
) {
    var openSheet by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    val onAddTag: (Int) -> Unit = { tagId ->
        filterTagList.add(tagId)
    }
    val onDeleteTag: (Int) -> Unit = { tagId ->
        filterTagList.remove(tagId)
    }
    val isTagContains: (Tags) -> Boolean = { tag ->
        filterTagList.contains(tag.id)
    }

    val selectedTagsAmount = remember {
        mutableStateOf(0)
    }

    val sheetState = rememberFlexibleBottomSheetState(
        flexibleSheetSize = FlexibleSheetSize(
            fullyExpanded = 0.6f,
            intermediatelyExpanded = 0.6f,
            slightlyExpanded = 0.6f
        ),
        allowNestedScroll = true,
        isModal = false
    )

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        ),
        title = {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                modifier = Modifier.size(150.dp)
            )
        },
        navigationIcon = {
            BadgedBox(
                badge = {
                    Badge(
                        containerColor = if(selectedTagsAmount.value > 0) Color.Red else Color.Transparent,
                        contentColor = Color.Black
                    ){
                        if(selectedTagsAmount.value > 0) {
                            Text(text = selectedTagsAmount.value.toString())
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    modifier = Modifier
                        .size(44.dp)
                        .clickable {
                            openSheet = true
                        }
                )
            }
        },
        actions = {
            BadgedBox(
                badge = {
                    Badge(
                        containerColor = if (cartProductsAmount > 0) Color.Red else Color.Transparent,
                        contentColor = Color.Black
                    ) {
                        if(cartProductsAmount > 0) {
                            Text(text = cartProductsAmount.toString())
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier
                        .size(44.dp)
                        .clickable {
                            navigator.navigate(NavRoutes.CartScreen.route)
                        }
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier
                    .size(44.dp)
                    .clickable {
                        navigator.navigate(NavRoutes.SearchScreen.route)
                    }
            )
        }
    )

    if(openSheet) {
        FlexibleBottomSheet(
            onDismissRequest = { openSheet = false },
            sheetState = sheetState,
            shape = MaterialTheme.shapes.large,
            containerColor = Color.White
        ) {
            LazyColumn(
                modifier = Modifier.padding(24.dp)
            ) {
                item {
                    Text(
                        text = "Подобрать блюда",
                        color = Color.Black,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                items(tags.value) { tag ->
                    CategoryItem(
                        tag = tag,
                        onAddTag = onAddTag,
                        onDeleteTag = onDeleteTag,
                        isTagContains = isTagContains,
                        selectedTagsAmount = selectedTagsAmount
                    )
                    HorizontalDivider()
                }
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        onFilter(filterTagList)
                        scope.launch {
                            sheetState.hide()
                        }
                        openSheet = false
                    },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Готово",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryItem(
    tag: Tags,
    onAddTag: (Int) -> Unit,
    onDeleteTag: (Int) -> Unit,
    isTagContains: (Tags) -> Boolean,
    selectedTagsAmount: MutableState<Int>
) {
    var isChecked by remember {
        mutableStateOf(isTagContains(tag))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                isChecked = !isChecked
                if (isChecked) {
                    onAddTag(tag.id)
                    selectedTagsAmount.value += 1
                } else {
                    onDeleteTag(tag.id)
                    selectedTagsAmount.value -= 1
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = tag.name,
            color = Color.Black
        )
        Checkbox(
            checked = isChecked,
            onCheckedChange = {
                isChecked = !isChecked
                if(isChecked) {
                    onAddTag(tag.id)
                    selectedTagsAmount.value += 1
                } else {
                    onDeleteTag(tag.id)
                    selectedTagsAmount.value -= 1
                }
                              },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = Color.LightGray,
                checkmarkColor = Color.White
            )
        )
    }
}