module mainMod

add(x: Number, y: Number) Number {
    return x + y;
}


struct Vector {
    x: Number;
    y: Number;
}

main() Void {
    var vector: Vector = Vector {
        x: 1;
        y: 2;
    };

    var aString = "A string literal";

    vector.x = 1;
    vector.y = 2;

    var number: Number = add(1, vector.x);
}