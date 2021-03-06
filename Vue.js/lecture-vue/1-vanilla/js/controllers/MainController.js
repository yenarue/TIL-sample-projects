import FormView from '../views/FormView.js';
import ResultView from '../views/ResultView.js';
import TabView from '../views/TabView.js';
import KeywordView from '../views/KeywordView.js';
import SearchModel from '../models/SearchModel.js';
import KeywordModel from '../models/KeywordModel.js';

const tag = '[MainController]';

export default {
    init() {
        console.log(tag, 'init()');
        FormView.setup(document.querySelector('form'))
            .on('@submit', e => this.onSubmit(e.detail.input))
            .on('@reset', e => this.onResetForm());

        TabView.setup(document.querySelector('#tabs'))
            .on('@change', e => this.onChangeTab(e.detail.tabName));

        KeywordView.setup(document.querySelector('#search-keyword'));
        ResultView.setup(document.querySelector('#search-result'));

        this.selectedTab = '추천 검색어';
        this.renderView();
    },

    // 그려주는 부분은 renderView()로 모두 위임
    renderView() {
        console.log(tag, 'renderView');
        TabView.setActiveTab(this.selectedTab);

        if (this.selectedTab === '추천 검색어') {
            this.fetchSearchKeyword();
        } else {

        }

        ResultView.hide();
    },

    fetchSearchKeyword() {
        KeywordModel.list().then(data => KeywordView.render(data));
    },

    search(query) {
        console.log(tag, "search()", query);
        SearchModel.list(query).then(data => {
            this.onSearchResult(data);
        });
    },

    onSubmit(input) {
        console.log(tag, "onSubmit()", input);
        this.search(input);
    },

    onResetForm() {
        console.log(tag, "onResetForm()");
        ResultView.hide();
    },

    onSearchResult(data) {
        ResultView.show();
        ResultView.render(data);
    },

    onChangeTab(tabName) {

    },
}