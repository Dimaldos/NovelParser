# Парсер новелл с интеа в кучу html страниц

В папке с ресурсами надо создать файл app.properties куда указать
```
sample=<путь к html шаблону>
output=<типовой путь для сохранения html страниц>
```
`output` должен содержать `%d` для указания номера и `.html` расширение. например:
```
output=C:\\folder\\ch-%d.html
```
