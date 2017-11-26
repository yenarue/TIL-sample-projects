const mongoose = require('mongoose');
mongoose.Promise = global.Promise;
const Schema = mongoose.Schema;
const autoIncrement = require('mongoose-auto-increment');

const ContactsSchema = new Schema({
    title : String,
    contents : String,
    author : String,
    created_at : {
        type : Date,
        default : Date.now()
    }
});

ContactsSchema.virtual('getDate').get(function() {
    const date = new Date(this.created_at);
    return {
        year : date.getFullYear(),
        month : date.getMonth() + 1,
        day : date.getDate()
    }
});

ContactsSchema.plugin( autoIncrement.plugin , { model : 'contacts' , field : 'seq' , startAt : 1 });
module.exports = mongoose.model('contacts', ContactsSchema);