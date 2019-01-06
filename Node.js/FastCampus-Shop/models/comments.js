const mongoose = require('mongoose');
mongoose.Promise = global.Promise;
const Schema = mongoose.Schema;
const autoIncrement = require('mongoose-auto-increment');

// <내 생각>
// MongoDB Collection 설계 방식을 고려해보면 이 구조는 조금 이상해 보일 수 있다.
// 마치 RDB에서처럼 foreign key를 가지는 느낌적인 느낌때문에 Products Collection과의 관계가 생기기 때문인데...
// 자, 내가 만드려는 앱이 쇼핑몰이라는 점을 다시한번 상기시켜보자.
// 관리자는 각 상품들에 달리는 댓글들에 대해 답변을 해야할 필요가 있을 것이다.
// 그러면 댓글들을 한번에 모아서 확인할 수 있는 관리자용 페이지가 필요할 것이다. 
// 이 때문에 이렇게 나누게 된 듯하다.
// 만약 위에서 언급한 기능이 필요 없다면, 사실 Comments는 Products Collection에 List형태로 추가되는 것이 더 낫다.
const CommentsSchema = new Schema({
    content : String,
    created_at : {
        type : Date,
        default : Date.now()
    },
    product_id : Number
});

CommentsSchema.plugin( autoIncrement.plugin , { model : 'comments' , field : 'id' , startAt : 1 });
module.exports = mongoose.model('comments', CommentsSchema);