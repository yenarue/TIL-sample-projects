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
    this.inputEl.addEventListener('keyup', e => this.onKeyUp(e))
}

FormView.onKeyUp = function (e) {
    this.showResetBtn(this.inputEl.value.length)
}

export default FormView