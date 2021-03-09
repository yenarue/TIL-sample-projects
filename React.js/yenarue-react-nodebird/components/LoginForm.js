import React, { useCallback } from 'react';
import { Form } from 'antd';
import Link from 'next/link';
import { Input, Button } from 'antd';
import styled from 'styled-components';
import { useDispatch } from "react-redux";

import useInput from '../hooks/useinput';
import { loginAction } from '../reducers/user';

// Styled Component
const ButtonWrapper = styled.div`
  margin-top: 10px; 
`

const FormWrapper = styled(Form)`
  padding: 10px;
`

// const LoginForm = ({ setIsLoggedIn }) => {
const LoginForm = () => {
  const dispatch = useDispatch();
  const [id, onChangeId] = useInput('');
  const [password, onChangePassword] = useInput('');

  // 아래와 같이 값을 캐싱해서 리렌더링을 막아도 괜찮음
  // const style = useMemo(() => ({ marginTop: 10px }), []);

  const onSubmitForm = useCallback((e) => {
    // e.preventDefault() // antd에는 이미 적용되어있음
    console.log(id, password);
    // setIsLoggedIn(true);
    dispatch(loginAction({ id, password }));
  }, [id, password]);

  // React Form 라이브러리도 많음 => 실무에서는 굳이 수작업으로 만들지마라
  return (
    <Form onFinish={onSubmitForm}>
      <div>
        <label htmlFor="user-id">아이디</label>
        <br/>
        <Input name="user-id" value={id} onChange={onChangeId} required/>
      </div>
      <div>
        <label htmlFor="use-password">비밀번호</label>
        <br/>
        <Input
          name="user-password"
          type="password"
          value={password}
          onChange={onChangePassword}
          required/>
      </div>
      {/*이렇게쓰면 리렌더링해버림 오브젝트 비교시 다른 것으로 인지되어서*/}
      {/*<div style={{ marginTop: 10 }}>*/}
      <ButtonWrapper>
        <Button type="primary" htmlType="submit" loading={false}>로그인</Button>
        <Link href="/signup"><a><Button>회원가입</Button></a></Link>
      </ButtonWrapper>
    </Form>
  )
}

// props가 필요하던 시절...
// LoginForm.propTypes = {
//   setisLoggedIn: PropTypes.func.isRequired,
// };

export default LoginForm;