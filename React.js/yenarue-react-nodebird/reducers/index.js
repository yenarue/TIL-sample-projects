import { HYDRATE } from "next-redux-wrapper";

// Default States
const initialState = {
  user: {
    isLoggedIn: false,
    user: null,
    signUpData: {},
    loginData: {},
  },
  post: {
    mainPosts: [],
  }
}

// Async Action Creator

// Actions Creator
export const loginAction = (data) => {
  return {
    type: 'LOGIN',
    data,
  }
}

export const logoutAction = () => {
  return {
    type: 'LOGOUT',
  }
}

// Actions
// const changeNickName = {
//   type: 'CHANGE_NICKNAME',
//   data: 'newNickName',
// }

// Reducers
// (이전 상태, 액션) => 다음 상태
const rootReducer = (state = initialState, action) => {
  console.log('actionType=', action.type);
  switch (action.type) {
    case HYDRATE:
      console.log('HYDRATE', action);
      return { ...state, ...action.payload };
    // case 'CHANGE_NICKNAME':
    //   return {
    //     ...state,
    //     name: action.data,
    //   }
    case 'LOGIN':
      return {
        ...state,
        user: {
          ...state.user,
          isLoggedIn: true,
          user: action.data,
        }
      }
    case 'LOGOUT':
      return {
        ...state,
        user: {
          ...state.user,
          isLoggedIn: false,
          user: null,
        }
      }
    default:
      return state;
  }
};

export default rootReducer;