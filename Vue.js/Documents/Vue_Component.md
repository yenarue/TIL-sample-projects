# Vue.js - Component

## 개요
대규모 앱의 경우에는 확장자가 .vue인 단일 파일 컴포넌트 (Single File Component) 형태로 개발되지만 이를 위해서는 Webpack이나 ES6에 대한 지식이 있어야 한다. 그러므로 일단은 컴포넌트 자체에 대해서만 알아보도록 하자.

## '컴포넌트(Component)' 란?
컴포넌트를 세마디로 정의하자면, **"작다."** **"스스로 자기 기능을 한다."** **"재사용이 가능하다."** 라고 할 수 있다.
이러한 컴포넌트들이 모여서 하나의 앱이 된다. 컴포넌트들은 부모-자식 관계로 트리를 형성한다.
![](https://kr.vuejs.org/images/components.png)
위 그림에서 오른쪽에 있는 초록색 상자 하나하나가 컴포넌트이며 왼쪽이 앱이다.

### 장점
컴포넌트 구조를 활용하였을 때, 얻을 수 있는 장점들은 다음과 같다.
* **재사용성이 뛰어나다** - 반복되는 UI/기능들을 재사용 할 수 있다.
* **테스트가 용이하다** - 컴포넌트 단위로 기능을 테스트할 수 있기 때문이다.
* **디버깅이 간편하다** - Vue devtools를 이용해 컴포넌트에 전달된 Props, 이벤트 등을 확인할 수 있다.

### 구현방법
```javascript
Vue.component('hello-component', {
  // 인라인 템플릿 - 추천하는 방법은 아니다.
  template : '<div>Hello, Component! :-)</div>'
})
```
```html
// 아래 방법을 더 추천한다
<template id="helloTemplate">
  <div>Hello, Component! :-)</div>
</template>
<script type="text/javascript">
  Vue.component('hello-component', {
    template : '#helloTemplate'
  })
</script>
```
본문 뷰프레임 부분에서는 위에서 정의한 컴포넌트를 아래와 같이 사용한다.
```html
<div id="app">
  <hello-component/>
  <hello-component/>
  <hello-component/>
</div>
```
위와 같이 컴포넌트들 작성 및 등록하고 등록한 태그명을 사용하여 html에 추가하면 구조가 갖춰진다.

Vue 인스턴스의 옵션을 컴포넌트에서도 동일한 방법으로 사용할 수 있다. 다만, `data` 옵션은 해당 컴포넌트의 **로컬 데이터를 저장하는 용도**로 사용된다. 즉, 해당 컴포넌트를 여러개 생성하면 각 컴포넌트들은 서로 다른 `data`를 가져야만 한다는 것이다. :-) 그렇기 때문에 컴포넌트의 `data`는 객체 인스턴스 형태로 작성될 수 없다. 객체 인스턴스 형태로 작성되면 생성된 컴포넌트 인스턴스들이 모두 같은 `data`를 가리키게 되기 때문이다.

그렇다면 컴포넌트에서는 `data` 옵션은 어떻게 정의해야 할까? 정답은 바로 **함수**이다!

```javascript
Vue.component('hello-component', {
    template : '#helloTemplate',
    data : function() {
      return {
        myData : 0
      };
    }
}
```
위와 같이 구현하면 컴포넌트 인스턴스 생성 시 마다 서로 다른 `data`인스턴스를 갖게되어 독립된 형태의 컴포넌트를 생성할 수 있다.

물론 동일한 `data`인스턴스를 갖는 컴포넌트를 만들고 싶다면 아래와 같이 구현하면 가능하긴하다. 하지만 정말 꼭 필요한 아주 특수한! 상황이 아니라면 사용하는 것을 지양하기를 추천한다....
```javascript
var data = { myData : 0 };
Vue.component('hello-component', {
    template : '#helloTemplate',
    data : function() {
      return data;
    }
}
```

### 주의사항
그 외에도 컴포넌트 사용시에는 몇가지 주의사항들이 존재한다.
#### 렌더링 문제
Vue 컴포넌트 렌더링보다 HTML 태그들에 대한 구문분석이 먼저 이루어진다. 그렇기 대문에 HTML구문 내에 Vue 컴포넌트를 넣으면 구문에러가 발생해 제대로 렌더링 되지 않는다. 이에 대한 세가지 해결방법이 있다.
1. `is` 애트리뷰트를 사용한다.
```html
<div id="app">
  <select>
    <option is="option-component"></option>
    <option is="option-component"></option>
  </select>
</div>
```
2. `<script type="text/x-template"></script>` 태그를 사용하여 템플렛으로 따로 뺀다.
```html
<script type="text/x-template">
	<select>
    	<!-- option-component 정의부는 생략한다... -->
        <option-component/>
        <option-component/>
    </select>
</script>
<script>
	Vue.component('select-template', {
		template: '#selectTemplate'
	})
</script>
<body>
<div id="app">
	<select-template/>
</div>
</body>
```
3. 단일 파일 컴포넌트(.vue파일)로 작성하는 경우에는 위와같이 사용하지 않아도 정상적으로 렌더링 된다.

#### 루트 엘리먼트는 단 한개만!
템플릿 정의 시, 루트 엘리먼트(Root Element)는 하나만 존재해야 한다.
```html
<template id="testTemplate">
	<div>
   		<p>Root Element must be only one!</p>
        <p>test template :-)</p>
    </div>
</template>
```

## 부모-자식 컴포넌트 관계
앞서 언급했듯이, 트리모양으로 구성된 컴포넌트들은 부모-자식간의 관계를 맺게된다. 아무리 부모-자식 간이라고 해도 컴포넌트는 독립체이기 때문에, 서로의  `option`값에 직접 접근할 수는 없다. 대신 Vue.js는 부모-자식 컴포넌트 간에 통신을 할 수 있도록 지원하고 있다. 부모-자식간의 통신은 **방향**에 따라 다른 방식으로 이루어지게 된다.

![](https://kr.vuejs.org/images/props-events.png)

* **부모 -> 자식** 방향으로는 `속성(Props)` 데이터를 전달(정확하게는 Pass)할 수 있다.
* **자식 -> 부모** 방향으로는 `이벤트(Event)`를 전달(정확하게는 Emit)한다. 이 때, 이벤트와 함께 데이터를 파라미터 형식으로 전달 할 수 있다.

### 부모 -> 자식 : `props`를 이용한 데이터 전달
작성중..

### 자식 -> 부모 : `event`를 이용한 데이터 전달
작성중..