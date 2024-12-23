# Telegram bot

<!-- TOC -->
* [Telegram bot](#telegram-bot)
  * [Hosting](#hosting)
    * [Настройка сервера](#настройка-сервера)
  * [Pipeline](#pipeline)
    * [Разработка](#разработка)
    * [Тестирование](#тестирование)
    * [Промышленная эксплуатация](#промышленная-эксплуатация)
  * [Секреты репозитория](#секреты-репозитория)
  * [Сопровождение](#сопровождение)
    * [БД](#бд)
    * [Подсказки](#подсказки)
<!-- TOC -->

## Hosting
### Настройка сервера

1. Арендовать VDS (параметры VDS поддаются обсуждению) на CentOS, главное, чтобы был 1 публичный IP-адрес, по которому можно подключаться к серверу через SSH + у сервера был доступ в интернет.
2. Сгенерировать пару SSH ключей себе на рабочей машинке через `ssh-keygen -t ed25519 -C "your_email@example.com"`
3. Ключ с расширением `.pub` - публичный, его надо добавить на сервер. Либо какой-то утилитой "Добавить SSH-ключ", если хостинг продвинутый, либо вручную:
Добавить ключ в конец файла `~/.ssh/authorized_keys`
4. Ключ без расширения - приватный, его добавить надо в github secrets.
4.1.* Если хочется серьезной безопасности, то необходимо отключить подключение по паролю через ssh:
В файле `/etc/ssh/sshd_config` изменить `PasswordAuthentication` на `PasswordAuthentication no`.
5. Установить docker с docker compose (ref -[официальная инструкция](https://docs.docker.com/engine/install/centos/) :
```bash
sudo yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine
sudo yum install -y yum-utils
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo yum install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
sudo systemctl start docker
```
Можно проверить успех мероприятия командой `sudo docker run hello-world`
6. Готово! можно запускать action [Build on PROD](https://github.com/Ligerina/TelegramBot/actions/workflows/deploy-on-prod.yml)

## Pipeline
### Разработка
Для разработки необходимо использовать ветки, названные в формате `dev/add-something`.

После пуша такой ветки в Github-репозиторий будет запушен [Github-action](.github/workflows/deploy-on-dev.yml), запускающий автотесты по доработанному приложению.

Если тесты успешно пройдены, то необходимо создать Pull Request (PR) и дождаться одобрения от другого участника проекта.


### Тестирование
После того, как PR dev-ветки был одобрен, её необходимо смержить в ветку `test`, после чего запуститься [Github-action](.github/workflows/deploy-on-test.yml), который прогонит автотесты, создаст docker image с тегом `test` и запушит его на Dockerhub.
Предполагается, что перед мержем в `master`- ветку этот image будет тщательным образом протестирован.

### Промышленная эксплуатация
После тестирования функционала в ветке `test`, её необходимо влить в ветку `master`. Соответствующий [github-action](.github/workflows/deploy-on-prod.yml)  прогонит автотесты, создаст docker image с тегом `latest`, запушит его на Dockerhub, обновит запущенный на сервере контейнер на актуальную версию.

## Секреты репозитория
Для корректной работы всех github-actions в репозиторий должны быть добавлены секреты.

| Название               | Суть                                                      |
|:-----------------------|-----------------------------------------------------------|
| SERVER_HOST            | Адрес хоста сервера                                       |
| SERVER_USER            | Имя пользователя, от лица которого подключаемся к серверу |
| SERVER_SSH_KEY         | SSH-ключ для подключения к серверу                        |
| SERVER_SSH_PORT        | Номер SSH-порта сервера                                   |
| DOCKER_HUB_USERNAME    | Логин в Dockerhub                                         |
| DOCKER_HUB_PASSWORD    | Пароль в Dockerhub                                        |
| TELEGRAM_BOT_TOKEN_PROD | Токен для Telegram бота                                   |
| TELEGRAM_BOT_NAME_PROD | Имя Telegram бота                                         |

## Сопровождение

### БД
Данные из БД хранятся в каталоге `/var/lib/docker/volumes/tg-bot-db-data` сервера.
Подключиться-поглядеть на БД можно в контейнере, через команду `psql --username=data --dbname=data`, запущенную в интерактивном режиме в контейнере.

### Подсказки
- Зайти в контейнер повторно можно через 
```bash
docker exec -it <имя_контейнера> bash
```
- Сразу войти в БД можно через: 
```bash
docker exec -it <имя_контейнера> psql --username=data --dbname=data
```