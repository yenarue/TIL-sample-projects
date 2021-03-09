import AppLayout from "../components/AppLayout";
import Head from "next/head";
import NicknameEditForm from "../components/NicknameEditForm";
import FollowList from "../components/FollowList";
import FollowerList from "../components/FollowList";

const Profile = () => {
  const followingList = [{nickName: '예나르'}, {nickName: '혜림언니'}, {nickName: '다이니'}];
  const followerList = [{nickName: '예나르2'}, {nickName: '혜림언니2'}, {nickName: '다이니2'}];

  return <>
    <Head>
      <title>내 프로필 | Yenarue</title>
    </Head>
    <AppLayout>
      <NicknameEditForm/>
      <FollowList header="팔로잉 목록" data={followingList}/>
      <FollowerList header="팔로워 목록" data={followerList}/>
    </AppLayout>
  </>
};

export default Profile