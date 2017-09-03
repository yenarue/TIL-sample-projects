var  a = 2 / "str";   // NaN

// a는 NaN (Not a Number) 이지만 타입은 "number"이다.
console.log(a);       // NaN
console.log(typeof a === "number")  // true
console.log("\n");

// NaN은 유일무이한 값으로서, 다른 NaN과 동등하지 않다. 심지어 자기 자신조차도...
console.log(a === a);             // false
console.log(a == NaN);            // false
console.log(a === NaN);           // false
console.log(NaN !== NaN);         // true

// 그러므로 isNaN을 이용하여 NaN여부를 체크하자
console.log(isNaN(a));   // true

// 그런데 좀 이상하다;; String도 NaN으로 체크한다.
var b = "str";
console.log(isNaN(b));  // true (?!)
// isNaN은 NaN타입을 검증하는 것이 아닌 말 그대로 숫자가 아닌 지를 체크하는 놈이다.

// 그러므로 Number.isNaN을 쓰도록 하자!
console.log(Number.isNaN(a)); // true
console.log(Number.isNaN(b)); // false
