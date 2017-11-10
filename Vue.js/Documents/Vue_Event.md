# Vue.js - 이벤트

## 이벤트 처리
이벤트 핸들링은 다음과 같이 이루어진다.
```html
<div id="clickme" v-on:click="onClick"></div>
<!-- 위 아래 모두 사용 가능 (@ : v-on의 Alias) -->
<div id="clickme" @click="onClick"></div>
```
```javascript
methods : {
	onClick : function(event) {
		console.log("### CLICK")
	}
}
```
clickme 영역을 마우스 클릭하면 onClick메서드가 호출된다.

## 이벤트 전파
### 이벤트 전파의 3단계
![이벤트 전파 과정](https://gccontent.blob.core.windows.net/gccontent/blogs/legacy/wijmo/2015/06/event-diagram.png)
##### **1. 포착 (Capturing)**
이벤트를 발생시킨 요소를 포착해서 그 요소로까지 파고 들어가는 이벤트 포착 단계. 아직 이벤트에 연결된 함수를 호출하지 않는다.
##### **2. 발생 (Raising)**
이벤트를 발생시킨 요소에 도착하여 이벤트에 연결된 함수를 **호출**시키는 이벤트 발생 단계
##### **3. 버블링 (Bubbling)**
이벤트가 발생한 요소로부터 상위요소로까지 다시 거슬러 올라가면서 **상위 요소들에게 동일한 이벤트를 호출**시키는 버블링 단계

### 이벤트 전파를 제어해보자
일단 전파 제어를 하기 전에 일반적인 전파 흐름을 확인해보도록 하자.
#### 일반적인 전파 흐름
inner 영역을 선택하면, Raising 단계에서 부터 이벤트가 발생하고, Bubbling 작용이 이루어지기 때문에 **innerClick -> outerClick** 순으로 호출된다.
```javascript
  <div id="outer" @click="outerClick">
    <div id="inner" @click="innerClick"></div>
  </div>
```

이제 진짜로 전파를 제어해보자!
#### stopPropagation()
Bubbling을 막아 Raising 단계까지만 전파되도록 한다.
아래의 코드를 실행한 후 내부를 클릭하면, **innerClick만 호출**된다.
```javascript
methods: {
	innerClick : function (event) {
        console.log("inner clicked!!);
    	event.stopPropagation();
    }
    outerClick: function (event) {
    	console.log("outer clicked!!);
    }
}
```

#### .stop 수식어
stopPropagation()와 동일한 기능을 한다.
아래의 코드를 실행한 후 내부를 클릭하면, **innerClick만 호출**된다.
```javascript
<div id="outer" @click="outerClick">
	<div id="inner" @click.stop="innerClick"></div>
</div>
```

#### .capture 수식어
포착(Capturing) 단계에서부터 이벤트가 발생한다. 즉, Outer에서 이벤트를 먼저 받게된다. **outerClick -> innerClick 순으로 호출된다**
```javascript
<div id="outer" @click.capture="outerClick">
	<div id="inner" @click="innerClick"></div>
</div>
```
#### .self 수식어
발생(Raising) 단계에서만 이벤트를 발생시키고 종료한다. 즉, Inner에서만 이벤트를 받게된다. **innerClick만 호출**된다.
```javascript
<div id="outer" @click.self="outerClick">
    <div id="inner" @click="innerClick"></div>
</div>
```
(수식어 이름이 self라 outerClick이 불릴 것 같지만 그렇지 않다....)

#### 그럼 outerClick만 호출하고 싶으면 어떻게하지?
위 수식어들을 중첩해서 사용하면 가능하다 :-)
```javascript
<div id="outer" @click.capture.stop="outerClick">
    <div id="inner" @click="innerClick"></div>
</div>
```
capture 수식어를 사용하여 Capturing단계에서부터 이벤트를 발생시키기 때문에 outerClick이 일단 호출된다. 그런데 바로 이어서 stop이 되기 때문에 Raising 단계까지 가지 못하고 종료된다. 즉, **Outer에서만 이벤트를 받게된다.**

## 그 외 수식어
#### .once
이벤트를 한번만 발생시킨다.

#### keycode 수식어
keycode를 받아오는 수식어. 물론수식어를 사용하지 않고 핸들러에서 event 객체를 받아 event.keyCode값을 확인해도 된다.
```html
<input type="text" v-model="search" v-on:keyup.13="onEnter"/>
```

#### 마우스 버튼 수식어
마우스 버튼 클릭을 받아오는 수식어. 왼쪽, 오른쪽 클릭을 받아올 수 있다.
```html
<input type="text" v-model="search" v-on:mouseup.left="onLeftMouse"/>
```