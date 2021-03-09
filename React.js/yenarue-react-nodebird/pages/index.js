// 디렉토리명 pages 는 예약어 => 리액트 페이지로 알아서 바꿔줌
// import React from 'react'

import AppLayout from "../components/AppLayout";

const Home = () => {
  return (
    <AppLayout>
      {/*여기가 곧 children*/}
      <div> Hello, Next! </div>
    </AppLayout>
  )
}

export default Home;