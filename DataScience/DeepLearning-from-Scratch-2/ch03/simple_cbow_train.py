from common.util import preprocess, create_contexts_target, convert_one_hot
from common.optimizer import Adam
from common.trainer import Trainer
from ch03.simple_cbow import SimpleCBOW

window_size = 1
hidden_size = 5
batch_size = 3
max_epoch = 1000

text = 'You say goodbye and I say hello.'
corpus, word_to_id, id_to_word = preprocess(text)
print(corpus)
print(id_to_word)

contexts, target = create_contexts_target(corpus, window_size=1)

print(contexts)
print(target)

vocab_size = len(word_to_id)
contexts = convert_one_hot(contexts, vocab_size)
target = convert_one_hot(target, vocab_size)

model = SimpleCBOW(vocab_size, hidden_size)
optimizer = Adam()
trainer = Trainer(model, optimizer)

trainer.fit(contexts, target, max_epoch, batch_size)
trainer.plot()

word_vecs = model.word_vecs
for word_id, word in id_to_word.items():
    print(word, word_vecs[word_id])