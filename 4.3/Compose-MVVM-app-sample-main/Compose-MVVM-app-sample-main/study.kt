package com.example.composemvvm
    package .datasource
        package .networking
            package .models
                + WeatherApiResponse.kt // data class 정의
                    - data class WeatherApiResponse, Daily, Temp, Current
            - class CoroutineDispatcherProvider // Coroutine IO, Default, Main 제공
            - interface NetworkingService // retrofit2 서버 통신용 API
        - class WeatherRepository @Inject constructor(private val networkingService: NetworkingService)


    package .di
    package .ui.theme

    + MainActivity.kt
        - @AndroidEntryPoint class MainActivity
        - @Composable class CurrentWeatherHeader
        - @Composable class WeatherLoadedScreen
        - @Composable class ErrorDialog

    - @HiltViewModel class MainViewModel
    - @HiltAndroidApp class WeatherApp