import React from 'react';
import PropTypes from 'prop-types';
import Link from 'next/link';
import { Menu, Input, Row, Col } from 'antd';
import styled from 'styled-components';
import { useSelector } from "react-redux";

import UserProfile from '../components/UserProfile';
import LoginForm from '../components/LoginForm';

// Styled Component
const SearchInput = styled(Input.Search)`
  vertical-algin: middle;
`;

const AppLayout = ({ children }) => {
  // Without Redux
  // const [isLoggedIn, setIsLoggedIn] = useState(false);
  const { isLoggedIn } = useSelector((state) => state.user);

  // Virtual DOM
  return (
    <div>
      <Menu mode="horizontal">
        <Menu.Item>
          <Link href='/'><a>예나르</a></Link>
        </Menu.Item>
        <Menu.Item>
          <Link href='/profile'><a>프로필</a></Link>
        </Menu.Item>
        <Menu.Item>
          <SearchInput enterButton />
        </Menu.Item>
        <Menu.Item>
          <Link href='/signup'><a>회원가입</a></Link>
        </Menu.Item>
      </Menu>

      {/*gutter : Col 사이의 간격*/}
      <Row gutter={8}>
        {/*sm : 태블릿 / lg, xl : 대화면
        관련 문서 : https://ant.design/components/grid/*/}
        <Col xs={24} md={6}>
          {/*                          데이터가 흩어져 있었기 때문에 하나씩 props로 넘겨줬다
            {isLoggedIn ? <UserProfile setIsLoggedIn={setIsLoggedIn} /> : <LoginForm setIsLoggedIn={setIsLoggedIn} /> }
          */
            isLoggedIn ? <UserProfile/> : <LoginForm/>
          }
        </Col>
        <Col xs={24} md={12}>
          {children}
        </Col>
        <Col xs={24} md={6}>
          <a href={"https://yenarue.github.io"} target="_blank" rel="noreferrer noopener">Mady by Yenarue</a>
        </Col>
      </Row>
    </div>
  );
};

AppLayout.propTypes = {
  children: PropTypes.node.isRequired,
};

export default AppLayout;