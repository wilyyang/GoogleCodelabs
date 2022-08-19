** Compose ** 

- class MainActivity : AppCompatActivity() -> JetnewsApp

package com.example.jetnews
    package data
        package interests
            package impl
                - class FakeInterestsRepository : InterestsRepository
                    * private val selectedTopics = MutableStateFlow(setOf<TopicSelection>())
                    * private val selectedPeople = MutableStateFlow(setOf<String>())
                    * private val selectedPublications = MutableStateFlow(setOf<String>())
                    * private val mutex = Mutex()

            + InterestsRepository.kt
                - data class InterestSection(val title: String, val interests: List<String>)
                - interface InterestsRepository // getTopics, getPeople, getPublications,
                                                    toggleTopicSelection, togglePersonSelected, togglePublicationSelected,
                                                    observeTopicsSelected, observePeopleSelected, observePublicationSelected
                - data class TopicSelection(val section: String, val topic: String)

        package posts
            package impl
                - class BlockingFakePostsRepository : PostsRepository
                    * private val favorites = MutableStateFlow<Set<String>>(setOf())
                - class FakePostsRepository : PostsRepository
                    * private val favorites = MutableStateFlow<Set<String>>(setOf())
                    * private val mutex = Mutex()
                + PostsData.kt // Define hardcoded posts
            - interface PostsRepository // getPost, getPostsFeed, observeFavorites, toggleFavorite

        + AppContainerImpl.kt
            - interface AppContainer { val postsRepository: PostsRepository, val interestsRepository: InterestsRepository }
            - class AppContainerImpl(private val applicationContext: Context) : AppContainer
        + Result.kt - sealed class Result

    package models
        + Post.kt
            - data class Post, Metadata, PostAuthor, Publication, Paragraph, Markup
        - data class PostsFeed // list Post

    package ui
        package article
            + ArticleScreen.kt
                > @Composable fun ArticleScreen(post: Post,isExpandedScreen: Boolean, onBack: () -> Unit,
                                                isFavorite: Boolean, onToggleFavorite: () -> Unit,
                                                modifier: Modifier = Modifier, lazyListState: LazyListState = rememberLazyListState())
                > @Composable fun ArticleScreenContent(post: Post, 
                                                        navigationIconContent: @Composable (() -> Unit)? = null, 
                                                        bottomBarContent: @Composable () -> Unit = { },
                                                        lazyListState: LazyListState = rememberLazyListState())
                > @Composable fun BottomBar(onUnimplementedAction: () -> Unit,
                                            isFavorite: Boolean, onToggleFavorite: () -> Unit,
                                            onSharePost: () -> Unit, modifier: Modifier = Modifier)
                > @Composable fun FunctionalityNotAvailablePopup(onDismiss: () -> Unit)
            + ArticleScreen.kt
                > @Composable fun PostContent(post: Post, modifier: Modifier = Modifier, state: LazyListState = rememberLazyListState())
                > @Composable fun PostHeaderImage(post: Post)
                > @Composable fun PostMetadata(metadata: Metadata)
                > @Composable fun Paragraph(paragraph: Paragraph)
                > @Composable fun CodeBlockParagraph(text: AnnotatedString, textStyle: TextStyle, paragraphStyle: ParagraphStyle)
                > @Composable fun BulletParagraph(text: AnnotatedString, textStyle: TextStyle, paragraphStyle: ParagraphStyle)
                > @Composable fun ParagraphType.getTextAndParagraphStyle(): ParagraphStyling 
        package components
            + AppNavRail.kt
                > @Composable fun JetnewsNavRail(modifier: Modifier = Modifier, header: @Composable (ColumnScope.() -> Unit)? = null, 
                                                    content: @Composable ColumnScope.() -> Unit)
                > @Composable fun AppNavRail(currentRoute: String, navigateToHome: () -> Unit, navigateToInterests: () -> Unit,
                                                modifier: Modifier = Modifier)
                > @Composable fun NavRailIcon(icon: ImageVector, contentDescription: String, isSelected: Boolean,
                                                action: () -> Unit, modifier: Modifier = Modifier,) 
            
            > @Composable fun JetnewsSnackbarHost(hostState: SnackbarHostState, modifier: Modifier = Modifier,
                                                    snackbar: @Composable (SnackbarData) -> Unit = { Snackbar(it) }) 
            + NavigationButtons.kt // JetnewsIcon, NavigationIcon
        package home
        package interests
        package modifiers
        package theme
        package utils
        + AppDrawer.kt
        + JetnewsApp.kt
        + JetnewsNavGraph.kt
        + JetnewsNavigation.kt
        - class MainActivity : AppCompatActivity() // -> JetnewsApp(appContainer, widthSizeClass)

    package utils
        - data class ErrorMessage(val id: Long, @StringRes val messageId: Int)
        > val LazyListState.isScrolled: Boolean
        > internal fun <E> MutableSet<E>.addOrRemove(element: E)
        > MultipreviewAnnotation.kt // 


    - class JetnewsApplication : Application() { lateinit var container: AppContainer = container = AppContainerImpl(this) }