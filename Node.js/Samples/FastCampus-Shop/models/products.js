const mongoose = require('mongoose');
mongoose.Promise = global.Promise;
const Schema = mongoose.Schema;
const autoIncrement = require('mongoose-auto-increment');

const ProductsSchema = new Schema({
    name : String,
    price : Number,
    description : String,
    created_at : {
        type : Date,
        default : Date.now()
    }
});

ProductsSchema.plugin( autoIncrement.plugin , { model : 'products' , field : 'id' , startAt : 1 });
module.exports = mongoose.model('products', ProductsSchema);