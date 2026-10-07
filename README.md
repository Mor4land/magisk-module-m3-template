# Magisk Module M3 Companion App & Module Template

<p align="center">
  <img src="https://img.shields.io/badge/Material_Design_3-Material_You-0061A4?style=for-the-badge&logo=materialdesign" alt="M3" />
  <img src="https://img.shields.io/badge/Root_Engine-TopJohnWu_Libsu_6.0-4CAF50?style=for-the-badge&logo=android" alt="Libsu" />
  <img src="https://img.shields.io/badge/Compat-Magisk%20%7C%20KernelSU%20%7C%20APatch-blueviolet?style=for-the-badge" alt="Compatibility" />
  <img src="https://img.shields.io/badge/Kotlin-2.0.20-7F52FF?style=for-the-badge&logo=kotlin" alt="Kotlin" />
</p>

Готовый шаблон современного Android-приложения (Companion App) и прошиваемого ZIP-модуля для **Magisk**, **KernelSU** и **APatch**, спроектированный строго по спецификациям [Material Design 3 (Material You)](https://m3.material.io/).

---

## 🌟 Основные возможности

### 🎨 Дизайн и UI (Material Design 3)
- **Dynamic Color (Monet):** автоматическая адаптация палитры под системные цвета и обои Android 12+ (`dynamicDarkColorScheme` / `dynamicLightColorScheme`) с откалиброванным baseline для предыдущих версий.
- **Tonal Elevation & Surfaces:** скругления `20-24dp`, тональные контейнеры `surfaceContainer`, отсутствие устаревших теней.
- **M3 Компоненты:** `CenterAlignedTopAppBar`, `NavigationBar`, `ElevatedCard`, `MaterialSwitch` с иконками в бегунке, `Slider`, чипы `FilterChip`, `AssistChip` и `ModalBottomSheet`.
- **Edge-to-Edge:** прозрачные статус-бар и панель навигации с корректным учетом системных отступов (`WindowInsets`).

### ⚡ Системная интеграция и Root (Libsu)
- **TopJohnWu Libsu 6.0:** официальный root-движок автора Magisk с флагом `FLAG_MOUNT_MASTER`.
- **Автоопределение окружения:** детектирование Magisk, KernelSU, APatch, версии менеджера и статуса SELinux (`Enforcing` / `Permissive`).
- **Мониторинг модуля:** проверка директории `/data/adb/modules/<id>`, парсинг `module.prop`, создание/удаление флага `disable` (безопасный режим).
- **Синхронизация настроек:** сохранение настроек из UI напрямую в `/data/adb/modules/<id>/config.sh` через root shell.
- **Инструменты и питание:** рестарт SystemUI, Soft Reboot (перезапуск Zygote), полное меню перезагрузки, вызов `action.sh` модуля и очистка кэшей памяти.
- **Живые логи:** чтение и фильтрация `service.log` в реальном времени.

---

## 📁 Структура репозитория

```
├── app/                                       # Исходный код Android Companion App
│   ├── build.gradle.kts                       # Зависимости Jetpack Compose, M3, Libsu
│   └── src/main/java/com/mirage/magisktemplate/
│       ├── TemplateApp.kt                     # Инициализация Libsu и Mount Master
│       ├── MainActivity.kt                    # Scaffold с M3 навигацией (5 вкладок)
│       ├── core/
│       │   ├── model/
│       │   │   ├── ModuleConfig.kt            # Главный конфиг: ID модуля, пути в /data/adb/modules
│       │   │   ├── ModuleInfo.kt              # Модель парсинга module.prop
│       │   │   ├── ModuleSettings.kt          # Синхронизация UI <-> config.sh
│       │   │   └── RootInfo.kt                # Модель менеджера root
│       │   └── root/
│       │       ├── ShellManager.kt            # Libsu корутины и команды
│       │       └── ModuleRepository.kt        # Работа с файлами модуля
│       └── ui/
│           ├── components/                    # M3 Switch, Slider, Chips, HeroCard, RebootSheet
│           ├── screens/                       # Dashboard, Settings, Terminal, Logs, About
│           ├── theme/                         # Цветовые токены M3, шрифты, динамические темы
│           └── viewmodel/                     # MainViewModel (StateFlow)
├── magisk-module/                             # Структура прошиваемого Magisk-модуля
│   ├── META-INF/com/google/android/
│   │   ├── update-binary                      # Установщик Magisk
│   │   └── updater-script                     # Маркер #MAGISK
│   ├── module.prop                            # Метаданные модуля
│   ├── customize.sh                           # Автоустановка APK компаньона при прошивке
│   ├── service.sh                             # Фоновый скрипт поздней загрузки (читает config.sh)
│   ├── post-fs-data.sh                        # Скрипт ранней инициализации
│   ├── action.sh                              # Скрипт кнопки Action
│   ├── system.prop                            # Системные свойства
│   └── config.sh                              # Дефолтная конфигурация
├── .github/workflows/build.yml                # Автосборка APK и ZIP в GitHub Actions
└── build.py                                   # Скрипт сборки и упаковки релизных файлов
```

---

## 🚀 Как адаптировать шаблон под свой модуль

### 1. Измените идентификатор модуля
В [`app/src/main/java/com/mirage/magisktemplate/core/model/ModuleConfig.kt`](file:///c:/Users/mlowers/magisk-module-m3-template/app/src/main/java/com/mirage/magisktemplate/core/model/ModuleConfig.kt):
```kotlin
const val MODULE_ID = "your_module_id"
const val MODULE_DISPLAY_NAME = "Your Module Name"
const val MODULE_AUTHOR = "Your Name"
```

В [`magisk-module/module.prop`](file:///c:/Users/mlowers/magisk-module-m3-template/magisk-module/module.prop):
```ini
id=your_module_id
name=Your Module Name
version=v1.0.0
versionCode=1
author=Your Name
description=Описание вашего модуля
```

### 2. Настройте параметры конфигурации
В [`ModuleSettings.kt`](file:///c:/Users/mlowers/magisk-module-m3-template/app/src/main/java/com/mirage/magisktemplate/core/model/ModuleSettings.kt) объявите нужные параметры. При нажатии «Save» они автоматически записываются в `/data/adb/modules/<MODULE_ID>/config.sh`.

В скриптах модуля ([`service.sh`](file:///c:/Users/mlowers/magisk-module-m3-template/magisk-module/service.sh) / [`action.sh`](file:///c:/Users/mlowers/magisk-module-m3-template/magisk-module/action.sh)) настройки загружаются командой:
```bash
if [ -f "$MODDIR/config.sh" ]; then
    . "$MODDIR/config.sh"
fi
```

### 3. Сборка модуля и приложения
Запустите скрипт:
```bash
python build.py
```
Скрипт автоматически соберет Release APK через Gradle, скопирует его в архив модуля и создаст готовый к прошивке ZIP в папке `dist/`:
- `dist/MagiskTemplate-M3-v1.0.0.apk` — автономный APK.
- `dist/MagiskTemplate-M3-v1.0.0.zip` — прошиваемый архив для Magisk / KernelSU / APatch.

---

## 📄 Лицензия
MIT License. Свободно для использования в любых модулях.
