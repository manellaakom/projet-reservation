<?php

namespace App\Enum;

enum SupportStatus: string
{
    case EN_COURS = 'en_cours';
    case RESOLU = 'resolu';
    case CLOTURE = 'cloture';

    public function label(): string
    {
        return match ($this) {
            self::EN_COURS => 'En cours',
            self::RESOLU => 'Résolu',
            self::CLOTURE => 'Clôturé',
        };
    }
}
