import React, {useState, useCallback} from 'react';
import { useDispatch } from "react-redux";
import { Card, Avatar, Button } from 'antd';

import { logoutAction } from "../reducers/user";

// const UserProfile = ({ setIsLoggedIn }) => {
const UserProfile = () => {
  const dispatch = useDispatch();

  const onLogOut = useCallback(() => {
    console.log('onLogout');
    // setIsLoggedIn(false);
    dispatch(logoutAction());
  }, []);

  return (
    <Card
      actions={[
        <div key="twit">짹짹<br/>0</div>,
        <div key="followings">팔로잉<br/>0</div>,
        <div key="follower">팔로워<br/>0</div>
      ]}
    >
      <Card.Meta
        avatar={<Avatar>Yenarue</Avatar>}
        title="Yenarue"
        />
      <Button onClick={onLogOut}>로그아웃</Button>
    </Card>
  )
}

export default UserProfile;