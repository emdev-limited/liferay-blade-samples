# Пример React портлета

Пример портлета, который использует
[React](https://reactjs.org/) для разработки пользовательских интерфейсов.

#
#### **Структура модуля:**
    > react-npm-portlet
        > src
            > main
                > java
                    > com.liferay.blade.npm.react.portlet
                        > ReactPortlet.java
                > resources 
                    > META-INF.resources
                        > css
                            index.scss
                        > lib
                            App.js
                            index.js
                        init.jsp
                        view.jsp
        .babelrc
        .gitignore
         bnd.bnd
         build.gradle
         package.json
         README.markdown  
         
Файл `index.js` - входная точка приложения. Содержит функцию, принимающую `elementId`, которая экспортируется наружу 
в файл `view.jsp` и там происходит запуск скрипта портлета с передачей параметра `<portlet:namespace />-root`.
После чего приложение помещается в блок div с id `<portlet:namespace />-root`.

Сам файл `index.js` указывается в `package.json` под ключом `main`. Ключ `name` - название портлета.
       
#

#### Для запросов на бек используется api/jsonws. ####
