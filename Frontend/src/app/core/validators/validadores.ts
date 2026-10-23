import { PathKind, SchemaPath, SchemaPathRules, requiredError, validate } from '@angular/forms/signals';

/** Equivale al @NotBlank de Spring: no vacío y no solo espacios. */
export function noBlanco<TPathKind extends PathKind = PathKind.Root>(
  path: SchemaPath<string, SchemaPathRules.Supported, TPathKind>,
  message: string,
) {
  validate(path, ({ value }) => (value().trim() === '' ? requiredError({ message }) : undefined));
}