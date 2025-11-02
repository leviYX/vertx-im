echo "GET http://your-api-endpoint.com/path" | \
vegeta attack -rate=1000 -duration=60s -workers=100 | \
tee results.bin | \
vegeta report使用vertx来实现一些聊天通讯
