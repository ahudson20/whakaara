# Whakaara - Android Alarm Clock App

This README provides an overview of the project, its features, and how to get started with the application.

## Screenshots
<p align="center">
    <img src="https://github.com/ahudson20/whakaara/assets/29796379/4ae970f3-4927-48d4-bc25-e17ceef557aa" width="200" height="450"/>
    <img src="https://github.com/ahudson20/whakaara/assets/29796379/3cf00a23-7daa-4051-a1c0-116e3b36c22b" width="200" height="450"/>
    <img src="https://github.com/ahudson20/whakaara/assets/29796379/e64eabd7-c8b3-40ff-ba52-7077f585edc5" width="200" height="450"/>
    <img src="https://github.com/ahudson20/whakaara/assets/29796379/d1970b7c-62c0-46f9-81ba-9412157cfa90" width="200" height="450"/>
    <img src="https://github.com/ahudson20/whakaara/assets/29796379/b8cc053f-55dc-4fbf-8d96-925b0e20bc8e" width="200" height="450"/>
    <img src="https://github.com/ahudson20/whakaara/assets/29796379/e5aa302e-d668-438d-887d-15e9806a04bd" width="200" height="450"/>
    <img src="https://github.com/ahudson20/whakaara/assets/29796379/18670eaa-1267-428b-8e17-c25fd5e06e26" width="200" height="450"/>
</p>

## Technologies Used

* **Room**: Room is used for local data persistence, providing a seamless storage solution for alarms and their configurations.
* **Jetpack Compose**: Jetpack Compose is utilized to create a modern and responsive user interface, simplifying UI development.
* **Hilt**: Hilt is employed for dependency injection, facilitating modularization and improving code maintainability.

## UI
The app was designed using [Material 3 guidelines](https://m3.material.io/).

The Screens and UI elements are built entirely using [Jetpack Compose](https://developer.android.com/jetpack/compose).

The app has two themes:

- Dynamic color - uses colors based on the [user's current color theme](https://material.io/blog/announcing-material-you)
- Default theme - uses predefined colors when dynamic color is not supported

Each theme also supports dark mode, as well as high and medium contrast.

## Modularization

The **Whakaara** app has been modularized.

<p align="center">
    <img width="769" alt="modularization" src="https://github.com/ahudson20/whakaara/assets/29796379/5d41f669-047f-43ac-a1b6-53ca691cea47">
</p>

## Feedback and Support

If you encounter any issues with Whakaara or have any feedback, please feel free to open an issue on the GitHub repository. I appreciate your support and contributions to making Whakaara even better.

## Contributing

I welcome contributions from the community to enhance Whakaara further. If you would like to contribute, please follow these guidelines:

* Fork the repository.
* Create your feature branch (git checkout -b feature/YourFeature).
* Commit your changes (git commit -am 'Add some feature').
* Push to the branch (git push origin feature/YourFeature).
* Create a new Pull Request.

## License

This project is licensed under the MIT License, allowing you to modify and distribute the application as per the terms of the license.
