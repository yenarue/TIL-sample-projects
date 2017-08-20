// 자바 스크립트에서는 0이 양수,음수 부호를 가질 수 있다.
// 방향성을 나타내기 위하여 추가된 개념으로서, 미지원 브라우저에서는 -0도 그냥 0으로 처리된다.

var posZero = 0 / 3;  // 0
var negZero = 0 / -3; // -0

console.log(posZero);
console.log(negZero);  // 지원하는 콘솔에서만 -0으로 출력된다고 한다.

// -0 를 문자열로 바꾸면 "0"이 된다
console.log(negZero.toString());      // "0"
console.log(JSON.stringify(negZero)); // "0"

// "-0" 을 숫자로 바꾸면 -0이 된다
console.log(+"-0");            // -0
console.log(Number("-0"));     // -0
console.log(JSON.parse("-0"))  // -0

// 비교연산은 어떨까? -> +0과 -0을 같은 값으로 취급한다.
console.log(posZero == negZero);  // true
console.log(posZero === negZero); // true
console.log(posZero > negZero);   // false

// 그렇다면 어떻게 -0를 구분해낼까?
function isNegZero(n) {
  n = Number(n);
  return (n === 0) && (1 / n === -Infinity);
}

console.log(isNegZero(-0));       // true
console.log(isNegZero(0));        // false
console.log(isNegZero(0 / 3));    // false
console.log(isNegZero(negZero));  // true
console.log(isNegZero(posZero));  // false

// -0를 만든 의도는 참 좋은데, 왜 -0를 구분할 수 있는 유틸성 함수를 왜 제공하지 않는걸까...
// Number의 함수로 제공해줬음 좋겠다.
// 고 생각했었는데 Object.is를 쓰면 좀 더 수월하게 -0 구분이 가능하다!
console.log(Object.is(negZero, -0)); // true
console.log(Object.is(negZero, 0));  // false
// Nan 도 마찬가지도 쉽게 구분가능
var NaNVar = 2 / "str";
console.log(Object.is(NaNVar, NaN));  // true
