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
                            index.css
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
#### **Описание как делать запросы и проверять их на текущем портале используя Chrome расширение:**

#### Для запросов на бек использовался ApolloClient. ####

1. Скачать расширение для Chrome (ALtair GraphQL Client): https://chrome.google.com/webstore/detail/altair-graphql-client/flnheeellpciglgpaodhkhmapeljopja?hl=en
2. Залогиниться на портале;
3. Выбрать тип запроса (POST);
4. Ввести URL запроса в формате: http://incomand72-dev.emdev.ru/o/graphql?p_auth=Z1gFwQ1j
   (Токен `p_auth` можно взять при помощи команды: Liferay.authToken);
5. Найти в строке поиска нужный запрос (например userAccounts);
6. В левой части меню ввести данные запроса;

#### **Пример:** ####
        query{
          userAccounts(page: 2, pageSize: 1){
            items {
                name,
                alternateName,
                id,
                image
            }
            page
            pageSize
            totalCount
          }
        }

7. Send request;
