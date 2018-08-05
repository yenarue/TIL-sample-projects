# S3의 정적 웹 호스팅 기능 사용해보기

## Usage
S3으로 정적 웹 사이트 띄우기

```bash
$ aws s3 mv s3://$BucketName

$ aws s3 cp ./helloworld.html s3://$BucketName/helloworld.html

# 정책 설정. 콘솔 웹페이지로 들어가서 설정해도 됨.
$ aws s3api put-bucket-policy --bucket $BucketName --policy ./bucketpolicy.json

# S3의 정적 웹 호스팅 기능 활성화
$ aws s3 website s3://$BucketName --index-document helloworld.html
```

[http://$BucketName.s3-website-$Region.amazonaws.com/](http://awsinaction-deploy-yenarue.s3-website-us-east-1.amazonaws.com/) 으로 접속하면 확인 가능.

## Clean Up

```bash
$ aws s3 rb --force s3://$BucketName
```
