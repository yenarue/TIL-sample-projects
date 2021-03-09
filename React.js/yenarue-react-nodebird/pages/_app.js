// pages의 공통 영역
import PropTypes from 'prop-types';
import Head from 'next/head';
import 'antd/dist/antd.css';

import wrapper from '../store/configureStore';

const App = ({ Component }) => {
  return (
    // <Provider store={store}>   // 여기서는 이 부분이 필요 없다 (next-redux-wrapper 가 알아서 감싸줌)
    <div>
      {/*html head 추가 가능*/}
      <Head>
        <meta charSet="utf-8"/>
        <title>Yenarue</title>
      </Head>
      <Component />
    </div>
    // </Provider>
  );

}

// 귀찮지만 점검하기를 추천 - 서비스 안정성 높아짐
App.propTypes = {
  Component: PropTypes.elementType.isRequired,
}

export default wrapper.withRedux(App);