package org.dava.validation;

public enum FileValidationMessages {
    FILE_TYPE_NOT_SPECIFIED_MESSAGE {
        public String getMessage() {
            return "File type is not specified.";
        }
    },
    INVALID_FILE_TYPE_MESSAGE {
        public String getMessage() {
            return "Invalid file type";
        }
    },
    EMPTY_FILE_MESSAGE {
        public String getMessage() {
            return "File is empty.";
        }
    },
    INVALID_FILE_MESSAGE {
        public String getMessage() {
            return "Invalid file provided.";
        }
    };

    public abstract String getMessage();
}
