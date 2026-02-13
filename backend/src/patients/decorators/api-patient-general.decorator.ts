import { applyDecorators } from '@nestjs/common';
import {
  ApiBadRequestResponse,
  ApiUnauthorizedResponse,
  ApiParam,
  ApiForbiddenResponse,
} from '@nestjs/swagger';

export function ApiPatientGeneral() {
  return applyDecorators(
    ApiParam({ name: 'id', description: 'Patient ID', type: Number }),
    ApiForbiddenResponse({ description: 'Forbidden' }),
    ApiUnauthorizedResponse({ description: 'Unauthorized' }),
    ApiBadRequestResponse({ description: 'Bad Request' }),
  );
}
