package com.example.composemvvm
    package .datasource
        package .networking
            package .models
                + WeatherApiResponse.kt // data class 정의
                    - data class WeatherApiResponse, Daily, Temp, Current
            - class CoroutineDispatcherProvider // Coroutine Dispatchers.IO, Default, Main 제공
            - interface NetworkingService // retrofit2 서버 통신용 API
        - class WeatherRepository @Inject constructor(private val networkingService: NetworkingService)
            > suspend fun fetchWeather(long: String, lat: String): WeatherApiResponse


    package .di
        - @Module @InstallIn(SingletonComponent::class) class AppModule
            > @Provides fun provideRetrofit(): NetworkingService
            > @Provides fun provideCoroutineDispatcher() = CoroutineDispatcherProvider()
    package .ui.theme
        + Color.kt, Shape.kt, Theme.kt, Type.kt
        + WeatherUiModel.kt
            - data class WeatherUiModel, WeatherForWeekItem

    + MainActivity.kt
        - @AndroidEntryPoint class MainActivity
        >> @Composable fun CurrentWeatherHeader(mainViewModel: MainViewModel = viewModel())
            >> MainViewModel.WeatherUiState.Error -> @Composable fun ErrorDialog(message: String)
            >> MainViewModel.WeatherUiState.Loaded -> @Composable fun WeatherLoadedScreen(data: WeatherUiModel)
        

    - @HiltViewModel class MainViewModel @Inject constructor(
        private val repository: WeatherRepository,
        @ApplicationContext private val applicationContext: Context,
        private val coroutineDispatcherProvider: CoroutineDispatcherProvider
            ) : ViewModel()
        * private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Empty)
        * val uiState: StateFlow<WeatherUiState> = _uiState
        > private fun fetchWeather() = _uiState.value = WeatherUiState.Loaded(WeatherUiModel(WeatherForWeekItem()))
        > private fun onQueryLimitReached() = _uiState.value = WeatherUiState.Error
        > private fun onErrorOccurred() = _uiState.value = WeatherUiState.Error
        - sealed class WeatherUiState
            * object Empty : WeatherUiState()
            * object Loading : WeatherUiState()
            - class Loaded(val data: WeatherUiModel) : WeatherUiState()
            - class Error(val message: String) : WeatherUiState()
    - @HiltAndroidApp class WeatherApp


    *** Hilt Code ***

            - class CoroutineDispatcherProvider // Coroutine Dispatchers.IO, Default, Main 제공
            - interface NetworkingService // retrofit2 서버 통신용 API
        - class WeatherRepository @Inject constructor(private val networkingService: NetworkingService)

        - @Module @InstallIn(SingletonComponent::class) class AppModule
            > @Provides fun provideRetrofit(): NetworkingService
            > @Provides fun provideCoroutineDispatcher() = CoroutineDispatcherProvider()

        - @AndroidEntryPoint class MainActivity
        >> @Composable fun CurrentWeatherHeader(mainViewModel: MainViewModel = viewModel())
        
    - @HiltViewModel class MainViewModel @Inject constructor(
        private val repository: WeatherRepository,
        @ApplicationContext private val applicationContext: Context,
        private val coroutineDispatcherProvider: CoroutineDispatcherProvider
            ) : ViewModel()
    - @HiltAndroidApp class WeatherApp


    *** ViewModel Code ***
            - data class WeatherUiModel, WeatherForWeekItem

        - @AndroidEntryPoint class MainActivity
        >> @Composable fun CurrentWeatherHeader(mainViewModel: MainViewModel = viewModel())
            >> MainViewModel.WeatherUiState.Error -> @Composable fun ErrorDialog(message: String)
            >> MainViewModel.WeatherUiState.Loaded -> @Composable fun WeatherLoadedScreen(data: WeatherUiModel)
        
    - @HiltViewModel class MainViewModel : ViewModel()
        * private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Empty)
        * val uiState: StateFlow<WeatherUiState> = _uiState
        > private fun fetchWeather() = _uiState.value = WeatherUiState.Loaded(WeatherUiModel(WeatherForWeekItem()))
        > private fun onQueryLimitReached() = _uiState.value = WeatherUiState.Error
        > private fun onErrorOccurred() = _uiState.value = WeatherUiState.Error
        - sealed class WeatherUiState
            * object Empty : WeatherUiState()
            * object Loading : WeatherUiState()
            - class Loaded(val data: WeatherUiModel) : WeatherUiState()
            - class Error(val message: String) : WeatherUiState()