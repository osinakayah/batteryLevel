import 'dart:convert';
import 'dart:typed_data';

class ElementResponse {
  final String message;
  final Uint8List imageByte;

  ElementResponse({
    this.message,
    this.imageByte,
  });
  factory ElementResponse.fromRawJson(String str) => ElementResponse.fromJson(json.decode(str));


  factory ElementResponse.fromJson(Map<String, dynamic> json) => ElementResponse(
    message: json["message"],
    imageByte: Base64Decoder().convert(json["imageByte"]),
  );
}
