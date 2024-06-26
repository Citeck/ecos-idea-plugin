# Плагин для IntelliJ IDEA для работы с платформой ECOS

## Функционал плагина

1. Создание новых проектов ECOS (приложение, микросервис)
2. Загрузка/выгрузка артефактов на/с локального сервера (формы ECOS, формы Alfresco, журналы, дашборды, процессы caseManagement, ноды и пр.).
3. Поиск артефактов по их идентификаторам (расширение для search everywhere).
4. Навигация в коде по артефактам ECOS/Alfresco через гиперссылки.
5. Автодополнение кода Java:
    Поиск и подстановка static объектов org.alfresco.service.namespace.QName по краткому наименованию.
6. Автодополнение кода JavaScript (Idea Ultimate):
    1. Cервер:
        - Типы данных Alfresco: properties, associations, types.
        - Бины baseJavaScriptExtension и их методы (например, caseStatusService).
    2. Формы ECOS:
        - Автодополнение объекта data списком компонент формы.
7. Навигация в файлах:
    - Формы (быстрый переход к компонентам по их имени).
    - Журналы (навигация по колонкам).
    - Модели данных Alfresco.
8. Консоль Alfresco JavaScript, возможность создания параметризированных скриптов и их быстрый вызов.
9. Инспекции кода:
    1. Формы - проверка задвоений ключей компонентов.
    2. Модели Alfresco - проверка импорта пространств имен.
10. Генерация кода:
     1. Формы - генерация компонентов формы из модели данных Alfresco
     2. Формы - генерация локализации для компонентов.
11. Расширения для LiveTemplates - функции для подстановки типов/свойств/ассоциаций Alfresco.
12. Расширения областей поиска файлами, содержащими артефакты ECOS/Alfresco.
13. Гиперссылки в консоли вывода для nodeRef с возможностью открытия их в браузере (карточка, nodeBrowser, старая карточка)
14. Language Injection (JavaScript) в формах ECOS

## Установка плагина в Idea
Скачать дистрибутив плагина можно из [release page](https://github.com/Citeck/ecos-idea-plugin/releases) или
собрать из исходников самостоятельно.

Далее установить плагин в Idea:\
**Settings -> Plugins -> Install Plugin from disk**

## Сборка дистрибутива плагина
1. Для корректной сборки дистрибутива плагина необходимо убедиться, что используемая версия Gradle JVM не ниже 17.\
Настроить используемую JVM для Gradle в Idea можно по следующему пути:\
**Settings -> Build, Execution, Deployment -> Build Tools -> Gradle -> Gradle JVM**
2. Сборка осуществляется Gradle задачей **buildPlugin**
3. Собранный дистрибутив будет расположен по следующему пути: **build/distributions/**

## Разработка
Для разработки плагина можно использовать Gradle задачу **runIde**\
При выполнении задачи будет запущен новый экземпляр Idea с пересобранным плагином.