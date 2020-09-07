# Image Fetcher

This starts up [LocalStack](https://github.com/localstack/localstack):

```shell script
TMPDIR=/private$TMPDIR docker-compose up
```

(The part `TMPDIR=/private$TMPDIR` is required only in MacOS.)

You can check the status of each service with the following:

```shell script
curl "http://localhost:4566/health?reload"
```

```shell script
aws --endpoint-url=http://localhost:4566 s3 ls s3://images
```

```shell script
aws --endpoint-url=http://localhost:4566 s3api head-object --bucket images --key es/cars/image1.png
```
```json
{
    "LastModified": "2020-09-07T09:25:00+00:00",
    "ContentLength": 1036366,
    "ETag": "\"7ef5a65ab373aa5e7344a1e9867ea8f0\"",
    "ContentType": "image/png",
    "Metadata": {
        "sourceurl": "http://example.org/assets/img/image1.png"
    }
}
```

## Acknowledges

Thanks to [Unsplash](https://unsplash.com/) for the sample images.

## References

* [How Can I Resize an Image Using Java?](https://www.baeldung.com/java-resize-image)
* [Working with Images in Java](https://www.baeldung.com/java-images)
