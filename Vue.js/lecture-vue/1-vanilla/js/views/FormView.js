import View from './View.js'

const tag = "[FormView]"

const FormView = Object.create(View)

// 내부적으로 html element 를 주입받음
FormView.setup = function (el) {
    this.init(el)
    this.inputEl = el.querySelector('[type=text]')
    this.resetEl = el.querySelector('[type=reset]')
    this.showResetBtn(false)
    this.bindEvents()
    return this
}

FormView.showResetBtn = function (show = true) {
    this.resetEl.style.display = show ? 'block' : 'none'
}

FormView.bindEvents = function () {
    this.on('submit', e => e.preventDefault())      // 브라우저의 Default Submit Action을 막기위함
    this.inputEl.addEventListener('keyup', e => this.onKeyUp(e))
    this.resetEl.addEventListener('click', e => this.onClickReset(e))
}

FormView.onKeyUp = function (e) {
    const enterKeyCode = 13
    this.showResetBtn(this.inputEl.value.length)

    if (!this.inputEl.value.length) this.emit('@reset')

    if (e.keyCode !== enterKeyCode) return
    this.emit('@submit', {input: this.inputEl.value})
}

FormView.onClickReset = function(e) {
    this.emit('@reset')
    this.showResetBtn(false)
}

export default FormView