export const initialState = {
  isLoggedIn: false,
  user: null,
  signUpData: {},
  loginData: {},
}

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

const reducer = (state = initialState, action) => {
  switch(action.type) {
    case 'LOGIN':
      return {
        ...state,
        isLoggedIn: true,
        user: action.data,
      }
    case 'LOGOUT':
      return {
        ...state,
        isLoggedIn: false,
        user: null,
      }
    default:
      return state;
  }
}

export default reducer;