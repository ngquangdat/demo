openssl genrsa -des3 -out private.pem 2048

openssl rsa -in private.pem -outform PEM -pubout -out public.pem

openssl pkcs8 -topk8 -in private.pem -out pkcs8private.pem -nocrypt

https://jxy.me/websocket-debug-tool/
