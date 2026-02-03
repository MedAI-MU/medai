import {
  registerDecorator,
  ValidationOptions,
  ValidationArguments,
} from 'class-validator';

export function IsRange(
  min: number,
  max: number,
  validationOptions?: ValidationOptions,
) {
  return function (object: object, propertyName: string) {
    registerDecorator({
      name: 'range',
      target: object.constructor,
      propertyName: propertyName,
      options: validationOptions,
      constraints: [min, max],
      validator: {
        validate(value: any, args: ValidationArguments) {
          if (typeof value !== 'number') return false;
          const [minValue, maxValue] = args.constraints as [number, number];
          return value >= minValue && value <= maxValue;
        },
        defaultMessage(args: ValidationArguments) {
          const [minValue, maxValue] = args.constraints as [number, number];
          return `${args.property} must be between ${minValue} and ${maxValue}`;
        },
      },
    });
  };
}
