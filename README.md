
loki pluginのインストール
```
docker plugin install grafana/loki-docker-driver:2.9.1 --alias loki --grant-all-permissions
```

ビルド＆起動
```
docker compose up -d
```

Grafana: http://localhost:3000
App: http://localhost:8080/ping

