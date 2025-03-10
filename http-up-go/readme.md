```shell
podman build -t http-up-go .
podman run -d -p 8080:8080 http-up-go
curl http://localhost:8080
```
