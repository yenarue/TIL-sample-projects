import { HYDRATE } from "next-redux-wrapper";
import { combineReducers } from "redux";
import user from './user';
import post from './post';

// Default States
const initialState = {
  user: {
  },
  post: {
    mainPosts: [],
  },
}

// Reducers
// (이전 상태, 액션) => 다음 상태
const rootReducer = combineReducers({
  // redux ssr를 위한 HYDRATE 용 인덱스 라우터 추가
  index: (state = {}, action) => {
    switch (action.type) {
      case HYDRATE:
        console.log('HYDRATE', action);
        return { ...state, ...action.payload };
      default:
        return state;
    }
  },
  user,
  post,
});
// initialState는 combineReducers가 알아서 합쳐줌

export default rootReducer;