function doSomething(){
	var a = 0;


	return function(){
		alert(++a);
	}
}

// a값을 2까지 출력하려면?
var func = doSomething()
func()
func()

// 다시 1부터 2까지 출력하려면?
func = doSomething()
func()
func()