// # Coercion
var a = 42;
var b = a + "";     // 암시적 강제변환
var c = String(a)   // 명시적 강제변환

// ## 추상 연산 - 1. toString
var a = [1, 2, 3];
console.log(a.toString());   // "1,2,3"

// JSON.stringify 는 직접적인 강제변환은 아니지만 ToString 강제변환과 연관된다.
// 1. 문자열, 숫자, boolean, null 값이 JSON으로 문자열화되는 방식이
//    ToString 추상연산의 규칙에 따라 문자열 값으로 강제변환되는 방식과 동일하다.
// 2. 전달된 객체가 toJSON()메소드를 가지고 있다면,
//    문자열화되기 전에 toJSON()이 자동으로 호출되어 JSON 안전 값으로 강제변환된다.
console.log(JSON.stringify(42));    // "42"
console.log(JSON.stringify("42"));  // ""42""
console.log(JSON.stringify(null));  // "null"
console.log(JSON.stringify(true));  // "true"

console.log(JSON.stringify(undefined));     // "undefined"
console.log(JSON.stringify(function(){}));  // "undefined"

// JSON.stringify는 인자가 undefined, 함수, 심벌 값이면 자동으로 누락시킨다.
console.log(
  JSON.stringify({ a:2, b:function(){} }) // "{"a":2}"
);
// 그게 배열안에 있으면 null로 치환시킨다.
console.log(
  JSON.stringify([1, undefined, function(){}, 4]) // "[1,null,null,4]"
);

// 환형참조?
var o = { };
var a = {
  b: 42,
  c: o,
  d: function(){}
}

o.e = a; // a를 환형참조로 만든다!
// console.log(JSON.stringify(a)); // TypeError!

// JSON으로 직렬화하는 함수를 정의하자
a.toJSON = function() {
  return { b: this.b }; // 직렬화에 b만 포함시킨다
}

console.log(a);   // "{"b":42}"

// toJSON : 문자열화하기 적당한 JSON 안전 값으로 바꾸는 것
//          JSON 문자열로 바꾸는 것이 아니다!
var a = {
  val: [1,2,3],
  // 올바른 사용법!
  toJSON: function() {
    return this.val.slice(1);
  }
};

var b = {
  val: [1,2,3],
  // 잘못된 사용법! JSON 문자열로 바꾸는게 아니야!
  toJSON: function() {
    return "[" + this.val.slice(1).join() + "]";
  }
};

console.log(JSON.stringify(a)); // "[2,3]"   // 각 요소가 JSON 포맷으로 출력
console.log(JSON.stringify(b)); // ""[2,3]"" // 문자열 그대로 출력

var a = {
  b: 42,
  c: "42",
  d: [1,2,3]
};

// JSON.stringify(object, replacer)
// - replacer : 배열, 함수 형태로 객체를 재귀적으로 직렬화 시킬 수 있음. (toJSON랑 비슷)
console.log(JSON.stringify(a, ["b", "c"]));     // "{"b":42,"c":"42"}"
console.log(JSON.stringify(a, function(k, v) {
  if (k !== "c") return v;
  // 특정 키를 빼고싶다면 아래와처럼 undefined 를 리턴해도 됨
  // if (k === "c") return undefined;
  // else return v;
})); // "{"b":42,"d":[1,2,3]}"

// JSON.stringify(object, replacer, space)
// - space : align (indent) 들여쓰기를 할 공간을 지정한다!
console.log(JSON.stringify(a, null, 4));
// {
//     "b": 42,
//     "c": "42",
//     "d": [
//         1,
//         2,
//         3
//     ]
// }
console.log(JSON.stringify(a, null, "----"));
// {
// ----"b": 42,
// ----"c": "42",
// ----"d": [
// --------1,
// --------2,
// --------3
// ----]
// }

// ## 추상 연산 - 2. ToNumber
// 숫자가 아닌 값 -> 수식 연산이 가능한 숫자
// true -> 1
// false -> 0
// undefined -> NaN
// null -> 0
var a = {
  valueOf: function() {
    return "42";
  }
}

var b = {
  toString: function() {
    return "42";
  }
}

var c = [4,2];
c.toString = function() {
  return this.join(""); // "42"
}

console.log(Number(a)); // 42
console.log(Number(b)); // 42
console.log(Number(c)); // 42
console.log(Number("")); // 0
console.log(Number([])); // 0
console.log(Number(["abc"])); // NaN

// ## 추상 연산 - 3. ToBoolean
// 자바스크립트에서는 true와 1이 동일하지 않다! (false와 0도 마찬가지)
// 강제변환은 가능하지만^^;

// Falsy 값 (False스러운 값ㅋㅋㅋ)
// : boolean으로 강제전환하면 false가 되는 값
// - undefined
// - null
// - false
// - +0, -0, NaN
// - ""
// 이외에는 모두 truthy하다. (모든 객체는 truthy함)

// Falsy 객체
// 모든 객체는 truthy하지만 특이케이스가 존재함.
// * 주의 : falsy한 값을 감싼 객체 레퍼를 뜻하는 건 아니다.
var a = new Boolean(false);
var b = new Number(0);
var c = new String("");
console.log(Boolean(a && b && c));  // true. 자바스크립트에서 모든 객체는 truthy 함!
// Falsy 객체는 순수 자바스크립트의 일부가 아님! IE관련 이슈로 인해서 생겨났다.
// 비표준 IE브라우저에서는 document.all을 사용하면 true로 반환했음 (객체니까)
// 하지만 document.all은 비표준이고, 오래전에 Deprecated되어 폐기되었음.
// 이 와중에 document.all이 true로 반환되면 IE브라우저이므로, 개발자들은 이를 체크하여 IE브라우저에 대한 로직을 넣었음.
// 이로부터 한참 뒤 IE가 표준을 준수하게 되었고 그때부터 위의 처리방식이 문제가 되기 시작한것임.
// 어마어마한 레거시로 인하여 대부분의 웹사이트가 비정상 동작을 하게될 위험에 빠짐
// 진퇴양난의 상황에서 IE 개발팀은 한가지 아이디어를 냄.
// '자바스크립트의 타입 체계를 살짝 바꿔서 document.all이 falsy인 것 처럼 돌아가게 하자!'
// 그렇게 추가된 falsy객체........... 물론 이는 비표준이다.
// 비표준 IE브라우저에 대한 서비스가 끊기면..... 이것도 언젠간 사라지겠지...ㅠㅠ

// Truthy 값
// : falsy 값 목록에 없으면 무조건 truthy 값 이다.
var a = "false";
var b = "0";
var c = "''";
console.log(Boolean(a && b && c));  // true. 문자열은 모두 truthy 하니까

var a = [];           // empty 배열
var b = {};           // empty 객체
var c = function(){}; // empty 함수
console.log(Boolean(a && b && c));  // true. 배열,객체,함수 모두 falsy 목록에 없으니까

// ## 명시적 강제변환 (Explicit Coercion) - 1. 문자열 <-> 숫자
var number = 42;
var string = "3.14";
// 인스턴스를 생성하는게 아님
var numberToString = String(number);  // ToStriung 로직에 따라 강제 변환
var stringToNumber = Number(string);  // ToNumber 로직에 따라 강제 변환
console.log(numberToString);   // "42"
console.log(stringToNumber);   // 3.14

// toString()을 사용하면 명시적이지만, 내부적으로 숫자값을 감싼 래퍼가 생성되므로 암시적인 동작도 함께 일어난다.
// 즉, 명시적으로, 암시적인 작동을 한다. (Explicitly Implicit)
numberToString = number.toString();
// 처음봤겠지만 아래의 +은 단항연산자이다! 명시적으로 숫자로 강제전환시킨다!! 덧셈에서 쓰이는 +랑은 다르다!
stringToNumber = +string;
console.log(numberToString);   // "42"
console.log(stringToNumber);   // 3.14

// 개인적인 의견으로는... +의 사용은 지양해야할 듯 하다.
// 동작상으로 이상있는 건 아니지만 남용하면 가독성에 문제가 생길 듯 하다.
string = "3.14";
var result = 5 + +string;
console.log(result);  // 8.14
// 으아아악!!!!ㅠㅠ
console.log(1 + - + + + - + 1); // 2
// 여기에 증감 연산자가 끼어들면 어떨까!
var a = 1;
console.log(1 + ++a - + + + - + 1 + a +++a); // 9
// 하.. 그냥 쓰지말자..

// ### 날짜 <-> 숫자
// 날짜 타입에 관한 강제변환은 추천하지 않는다고 한다. 이유는 아래는 보면 알게될 것이다.
var date = new Date('Sunday, August 27, 2017 7:52:36 AM GMT+09:00');
console.log(+date); // + 단항연산자를 사용하여 간편하게 timestamp로 변경할 수 있다.
var timestamp = +new Date(); // 관용적으로 이렇게도 많이 쓴다고한다.
// [개인생각] 하지만 나는 가독성을 굉장히 중요하게 생각하기 때문에.. Number()로 감싸는게 훨 좋아보인다!
console.log(Number(date));  // [개인생각] 난 그래도 이게 좋아보인다.
// 가독성이 구리다는 생각을 하는 사람이 많긴 하다보니 아래와 같은 방법을 더 추천한다고 한다.
var timestamp = new Date().getTime();
var timestamp = Date.now();

// ### 틸트(~)
// : 32비트 숫자로 강제변환 한 후 NOT 연산을 한다.
//   헷깔리는 연산자의 대명사이다보니 다들 잘 안쓰려고 한다.
// 수학에서의 ~는 2의 보수이다. 즉, ~x 은 -(x + 1) 쯤 된다.
console.log(~42); // -43

// 그런데 ~을 어디서 써야할까?
// -1과 같은 경계값을 체크하여 로직이 나뉘는 경우 유용하게 쓸 수 있다.
var a = "Hello World";
console.log(~a.indexOf("lo"))  // -4 (truthy)
if (~a.indexOf("lo")) { // a.indexof("lo") >= 0 보다 낫다
  console.log("lo를 찾았다!");
} else {
  console.log("lo를 못찾았다ㅠㅠ")
}
if (~a.indexOf("ol")) { // a.indexof("ol") == -1 보다 낫다
  console.log("ol를 찾았다!");
} else {
  console.log("ol를 못찾았다ㅠㅠ");
}

// [개인생각]
// 위의 방법은 굉장히 유용하긴한데 ~를 잘 이해하지 못한 사람이 보면 난감해하지 않을까 싶기도 하다.
// 실제 코드에서는 아래와 같이 한번 함수로 감싸서 가독성을 높히는 게 좋을 듯 하다.
function isThere(str, subStr) {
  return Boolean(~str.indexOf(subStr))
}
console.log(isThere("Hello World", "lo"));  // true
console.log(isThere("Hello World", "ol"));  // false

// ### 더블 틸트(~~)
// : 틸트가 두번 쓰인 것으로, 32비트 숫자로 강제변환 한 후 비트를 거꾸로 뒤집고 다시 뒤집는다.
//   두번 뒤집으니 원래상태로 돌아오기 때문에,
//   결과적으로는 32비트로 잘라내기만 한 것이 된다. (= 0으로 OR연산을 한 것과 같아짐)
// 가끔 Math.floor와 동일한 개념으로 착각하기도 하는데, 음수에서는 다르다!
var negFloat = -12.34
console.log(Math.floor(negFloat));  // -13
console.log(~~negFloat);            // -12
// 사실 연산 속도자체는 비트연산인 x | 0가 더 빠르다.
console.log(negFloat | 0);          // -12
// 하지만 연산자 우선순위때문에 ~~를 쓴다고 한다.
console.log(~~negFloat / 10);       // -1.2
console.log(negFloat | 0 / 10);     // -12
console.log((negFloat | 0) / 10);   // -1.2
// [개인생각] 32비트, 64비트 일 때 동작이 좀 달라지는 것도 그렇고,
//          Math.float, x | 0 연산 등으로 충분히 역할을 대체할 수 있는데
//          굳이 위의 리스크를 감수하고 가독성도 좋지 않은 ~~를 쓸 이유를 모르겠다.
//          일단 원리를 잘 알고 있으면 나중에 코드리딩엔 도움이 될 듯

// ## 명시적 강제변환 (Explicit Coercion) - 2. 문자열에서 숫자 파싱
var a = "42";
var b = "42px";
console.log(Number(a));    // 42
console.log(parseInt(a));  // 42
console.log(Number(b));    // NaN
console.log(parseInt(b));  // 42
// parseInt는 문자열을 위한 함수이다. 문자열이 아닌 값을 넘기면 암시적으로 문자열 강제변환이 된다.
// 두번째 인자로는 N진수를 받는다. 디폴트로는 x면 16진수, 0이면 8진수로 인식하기 때문에 때에 따라서는 이 인자를 넣어줘야 의도대로 동작할 것이다!
var hour = parseInt("08", 10);
var min = parseInt("05", 10);
console.log(hour);  // 8
console.log(min);   // 5

// ## 명시적 강제변환 (Explicit Coercion) - 3. Non-Boolean -> Boolean
// Boolean() : ToBoolean 에 의하여 불리언 값으로 강제변환 된다.
var a = "0";
Boolean(a); // true
// ! : Boolean 값을 뒤집는 단항연산자. falsy와 truthy도 뒤바꿔버린다.
// 그래서 Boolean()을 안쓰고 강제변환을 할때에는 !!를 사용하여 이중부정을 한다고 한다.
var a = "0";
!!a // true
