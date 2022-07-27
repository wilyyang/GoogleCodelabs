package com.example.codelabcomposelayout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberImagePainter
import com.example.codelabcomposelayout.ui.theme.CodelabComposeLayoutTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CodelabComposeLayoutTheme {
                ConstraintLayoutContent()
            }
        }
    }
}

val topics = listOf(
    "Arts & Crafts", "Beauty", "Books", "Business", "Comics", "Culinary",
    "Design", "Fashion", "Film", "History", "Maths", "Music", "People", "Philosophy",
    "Religion", "Social sciences", "Technology", "TV", "Writing"
)

@Composable
fun TwoTexts(modifier: Modifier = Modifier, text1:String, text2: String){
    Row(modifier = modifier.height(IntrinsicSize.Min)){
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
                .wrapContentWidth(Alignment.Start),
            text = text1
        )
        Divider(color = Color.Black, modifier = Modifier
            .fillMaxHeight()
            .width(1.dp))
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
                .wrapContentWidth(Alignment.End),
            text = text2
        )
    }
}

@Preview
@Composable
fun TwoTextsPreview(){
    CodelabComposeLayoutTheme {
        Surface{
            TwoTexts(text1 = "Hi", text2 = "there")
        }
    }
}

@Composable
fun DecoupledConstraintLayout(){
    BoxWithConstraints {
        val constraints = if (maxWidth < maxHeight){
            decoupledConstraints(margin = 16.dp)
        }else{
            decoupledConstraints(margin = 32.dp)
        }

        ConstraintLayout(constraints) {
            Button(
                onClick = {},
                modifier = Modifier.layoutId("button")
            ){
                Text("Button")
            }

            Text("Text", Modifier.layoutId("text"))
        }
    }
}

private fun decoupledConstraints(margin: Dp): ConstraintSet {
    return ConstraintSet {
        val button = createRefFor("button")
        val text = createRefFor("text")

        constrain(button){
            top.linkTo(parent.top, margin = margin)
        }
        constrain(text){
            top.linkTo(button.bottom, margin)
        }
    }
}

@Preview
@Composable
fun DecoupledConstraintLayoutPreview(){
    CodelabComposeLayoutTheme{
        DecoupledConstraintLayout()
    }
}

@Composable
fun LargeConstraintLayout(){
    ConstraintLayout {
        val text = createRef()

        val guideline = createGuidelineFromStart(fraction = 0.5f)
        Text(
            "This is a very very very very very very very long text",
            Modifier.constrainAs(text){
                linkTo(start = guideline, end = parent.end)
                width = Dimension.preferredWrapContent
            }
        )

    }
}

@Preview
@Composable
fun LargeConstraintLayoutPreview(){
    CodelabComposeLayoutTheme{
        LargeConstraintLayout()
    }
}

@Composable
fun ConstraintLayoutContent(){
    ConstraintLayout{
        val (button1, button2, text) = createRefs()

        Button(
            onClick = {},
            modifier = Modifier.constrainAs(button1){
                top.linkTo(parent.top, margin = 16.dp)
            }
        ) {
            Text("Button 1")
        }

        Text("Text", Modifier.constrainAs(text) {
            top.linkTo(button1.bottom, margin = 16.dp)
            centerAround(button1.end)
        })
        val barrier = createEndBarrier(button1, text)
        Button(
            onClick = {},
            modifier = Modifier.constrainAs(button2){
                top.linkTo(parent.top, margin = 16.dp)
                start.linkTo(barrier)
            }
        ){
            Text("Button 2")
        }
    }
}

@Preview
@Composable
fun ConstraintLayoutContentPreview(){
    CodelabComposeLayoutTheme{
        ConstraintLayoutContent()
    }
}

@Composable
fun LayoutsCodelab(){
    Scaffold (
        topBar = {
            TopAppBar (
                title = {
                    Text(text = "LayoutCodelab")
                },
                actions = {
                    IconButton(onClick = {}){
                        Icon(Icons.Filled.Favorite, contentDescription = null)
                    }
                }
            )
        }
    ){ innerPadding ->
        BodyContent(
            Modifier
                .padding(innerPadding)
                .padding(8.dp))
    }
}

@Composable
fun BodyContent(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(color = Color.LightGray, shape = RectangleShape)
            .padding(16.dp)
            .size(200.dp)
            .horizontalScroll(rememberScrollState()),
        content = {
            StaggeredGrid {
                for (topic in topics) {
                    Chip(modifier = Modifier.padding(8.dp), text = topic)
                }
            }
        })
}

@Composable
fun StaggeredGrid(
    modifier: Modifier = Modifier,
    rows: Int = 3,
    content: @Composable () -> Unit
){
    Layout(
        modifier = modifier,
        content = content
    ){  measurables, constraints ->
        val rowWidths = IntArray(rows) { 0 }
        val rowHeights = IntArray(rows) { 0 }
        val placeables = measurables.mapIndexed { index, measurable ->
            val placeable = measurable.measure(constraints)

            val row = index % rows
            rowWidths[row] += placeable.width
            rowHeights[row] = Math.max(rowHeights[row], placeable.height)

            placeable
        }

        val width = rowWidths.maxOrNull()?.coerceIn(constraints.minWidth.rangeTo(constraints.maxWidth))?: constraints.minWidth
        val height = rowHeights.sumOf { it }.coerceIn(constraints.minHeight.rangeTo(constraints.maxHeight))

        val rowY = IntArray(rows) { 0 }
        for (i in 1 until rows){
            rowY[i] = rowY[i-1] + rowHeights[i-1]
        }

        layout(width, height){
            val rowX = IntArray(rows){ 0 }
            placeables.forEachIndexed { index, placeable ->
                val row = index % rows
                placeable.placeRelative(
                    x = rowX[row],
                    y = rowY[row]
                )
                rowX[row] += placeable.width
            }
        }
    }
}

@Composable
fun Chip(modifier: Modifier = Modifier, text:String){
    Card (
        modifier = modifier,
        border = BorderStroke(color = Color.Black, width = Dp.Hairline),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Box(
                modifier = Modifier
                    .size(16.dp, 16.dp)
                    .background(color = MaterialTheme.colors.secondary)
            )
            Spacer(Modifier.width(4.dp))
            Text(text = text)
        }
    }
}

@Preview
@Composable
fun ChipPreview(){
    CodelabComposeLayoutTheme {
        Chip(text = "Hi there")
    }
}

@Preview
@Composable
fun LayoutCodelabPreview(){
    CodelabComposeLayoutTheme{
        LayoutsCodelab()
    }
}

@Preview
@Composable
fun BodyContentPreivew(){
    CodelabComposeLayoutTheme{
        BodyContent()
    }
}


@Composable
fun BodyContent2(modifier: Modifier = Modifier){
    MyOwnColumn(modifier.padding(8.dp)) {
        Text("MyOwnColumn")
        Text("places items")
        Text("vertically")
        Text("We've dont it by hand!")
    }
}

@Composable
fun MyOwnColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
){
    Layout(
        modifier = modifier,
        content = content
    ){  measurables, constraints ->
        val placeables  = measurables.map {  measurable ->
            measurable.measure(constraints)
        }

        var yPosition = 0

        layout(constraints.maxWidth, constraints.maxHeight){
            placeables.forEach{ placeable ->
                placeable.placeRelative(x = 0, y = yPosition)
                yPosition += placeable.height
            }
        }
    }
}

fun Modifier.firstBaselineToTop(
    firstBaselineToTop: Dp
) = this.then(
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
        val firstBaseline = placeable[FirstBaseline]

        val placeableY = firstBaselineToTop.roundToPx() - firstBaseline
        val height = placeable.height + placeableY
        layout(placeable.width, height){
            placeable.placeRelative(0, placeableY)
        }
    }
)

@Preview
@Composable
fun TextWithPaddingToBaselinePreview(){
    CodelabComposeLayoutTheme {
        Text("Hi there!", Modifier.firstBaselineToTop(32.dp))
    }
}

@Preview
@Composable
fun TextWithNormalPaddingPreview(){
    CodelabComposeLayoutTheme {
        Text("Hi there!", Modifier.padding(top = 32.dp))
    }
}

@Composable
fun ScrollingList(){
    val listSize = 100
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    Column{
        Row {
            Button(onClick = {
                coroutineScope.launch {
                    scrollState.animateScrollToItem(0)
                }
            }){
                Text("Scroll to the top")
            }

            Button(onClick = {
                coroutineScope.launch {
                    scrollState.animateScrollToItem(listSize -1)
                }
            }){
                Text("Scroll to the end")
            }
        }

        LazyColumn(state = scrollState){
            items(100){
                ImageListItem(it)
            }
        }
    }
}

@Composable
fun ImageListItem(index: Int){
    Row(verticalAlignment = Alignment.CenterVertically){
        Image(
           painter = rememberImagePainter(
               data = "https://developer.android.com/images/brand/Android_Robot.png"
           ),
            contentDescription = "Android Logo",
            modifier = Modifier.size(50.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text("Item #$index", style = MaterialTheme.typography.subtitle1)
    }
}

@Preview
@Composable
fun ImageListPreview() {
    CodelabComposeLayoutTheme {
        ScrollingList()
    }
}

@Composable
fun PhotographerCard(modifier: Modifier = Modifier) {
    Row (
        modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colors.surface)
            .clickable(onClick = {})
            .padding(16.dp)
    ){
        Surface(
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
        ) {

        }
        Column (modifier = Modifier
            .padding(start = 8.dp)
            .align(Alignment.CenterVertically)
        ) {
            Text("Alfred Sisley", fontWeight = FontWeight.Bold)
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text("3 minutes ago", style = MaterialTheme.typography.body2)
            }
        }
    }

}

@Preview
@Composable
fun PhotographerCardPreview() {
    CodelabComposeLayoutTheme {
        PhotographerCard()
    }
}