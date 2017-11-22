# Mongoose에서 Promise관련 Deprecated Warning이 뜨는 경우 해결법

## 원인
Mongoose에서는 기존에 `mPromise`를 사용하고 있었으나 이것이 Deprecated 되었으니 다른 `Promise`를 사용하라는 경고 문구이다.
일단 당장은 로직에 문제가 생기거나 하진 않지만 Deprecated 되었으니 기왕이면 잡아주도록 하자.

## 해결법
사실은 간단하다. `mPromise`를 사용하는 model쪽에 아래와 같이 넣어주면 된다.
```javascript
mongoose.Promise = global.Promise;
```
model쪽에서 mongoose를 require하고 있지 않고, db파일을 따로 빼서 단일 인스턴스로 넘겨주는 방식이라면 db파일에서만 위의 설정을 해줘도 무관하다.