echo "GET http://your-api-endpoint.com/path" | \
vegeta attack -rate=1000 -duration=60s -workers=100 | \
tee results.bin | \
vegeta report使用vertx来实现一些聊天通讯

chat_record
    chat_id: keyword
    form: keyword
    to: keyword
    content: text 分词 ik
    chat_time: date
    create_time: date
    status: int  1:已读 0:未读
    delete_status: int 逻辑删除 1:已删除 0:未删除




    
