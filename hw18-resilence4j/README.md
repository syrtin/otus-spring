# Zuul, Hystrix Circuit Breaker, Sleuth, Zipkin, Hystrix Dashboard, Secure Configuration Properties

## Обернуть внешние вызовы в Hystrix (Resilence4j)

### Цель:
Цель: сделать внешние вызовы приложения устойчивыми к ошибкам
Результат: приложение с изолированными с помощью Hystrix (Resilence4j) внешними вызовами

### Описание
1. Обернуть все внешние вызовы в Hystrix, Hystrix Javanica.онтейнере рекомендуется, но не обязательна.
2. Возможно использование Resilence4j
3. Возможно использование Feign Client
