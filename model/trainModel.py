import tensorflow as tf
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Conv2D, Flatten, Dropout, MaxPooling2D

from getData import loadDataset, IMG_HEIGHT, IMG_WIDTH

#constants
batchSize = 1
epochs = 2
times = 10

trainGen, valGen = loadDataset(batchSize)
print(len(trainGen))
print(len(valGen))

model = Sequential([
    Conv2D(16, 4, padding='same', activation='relu', input_shape=(IMG_HEIGHT, IMG_WIDTH ,3)),
    MaxPooling2D(),
    Dropout(0.2),
    Conv2D(32, 4, padding='same', activation='relu'),
    MaxPooling2D(),
    Conv2D(32, 4, padding='same', activation='relu'),
    MaxPooling2D(),
    Dropout(0.2),
    Flatten(),
    Dense(256, activation='relu'),
    Dense(2)
])

#model = tf.keras.models.load_model("workingModel.h5")
model.compile(optimizer='adam',
              loss=tf.keras.losses.BinaryCrossentropy(from_logits=True),
              metrics=['accuracy'])

for i in range(times):
    history = model.fit_generator(
        trainGen,
        epochs=epochs,
        validation_data=valGen,
    )

    model.save("workingModel" + str(i) + ".model")
