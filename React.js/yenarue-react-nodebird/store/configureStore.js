import { createWrapper } from 'next-redux-wrapper';
import { createStore, applyMiddleware, compose } from 'redux';
import { composeWithDevTools } from 'redux-devtools-extension';

import reducer from '../reducers';

const configureStore = () => {
  const middlewares = [];
  // redux의 기능이 확장된 것이라 enhancer
  const enhancer = process.env.NODE_ENV === 'production' ? compose(applyMiddleware(...middlewares)) : composeWithDevTools(applyMiddleware(...middlewares));

  const store = createStore(reducer, enhancer);   // store: state와 reducer를 포함한 것

  store.dispatch({
    type: 'CHANGE_NICKNAME',
    action: 'newNickName'
  });

  return store;
};

const wrapper = createWrapper(configureStore, {
  debug: process.env.NODE_ENV === 'development'
});

export default wrapper;