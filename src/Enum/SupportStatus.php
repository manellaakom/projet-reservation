<?php

namespace App\Enum;

enum SupportStatus: string
{
    case EN_ATTENTE = 'EN_ATTENTE';
    case EN_COURS = 'EN_COURS';
    case RESOLU = 'RESOLU';
    case FERME = 'FERME';
}