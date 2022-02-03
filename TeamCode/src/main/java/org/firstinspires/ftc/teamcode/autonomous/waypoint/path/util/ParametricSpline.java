package org.firstinspires.ftc.teamcode.autonomous.waypoint.path.util;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.geometry.Point;
import org.firstinspires.ftc.teamcode.autonomous.localization.Position;

public class ParametricSpline {
    public PolynomialSplineFunction xSpline;
    public PolynomialSplineFunction ySpline;
    private final PolynomialSplineFunction xPrimet;
    private final PolynomialSplineFunction yPrimet;
    public double splineDistance;

    public ParametricSpline(PolynomialSplineFunction f0, PolynomialSplineFunction f1, double dist)
    {

        xSpline = f0;
        ySpline = f1;
        xPrimet = xSpline.polynomialSplineDerivative();
        yPrimet = ySpline.polynomialSplineDerivative();
        splineDistance = dist;
    }

    public double getDerivative(double t)
    {
        return yPrimet.value(t) / xPrimet.value(t);
    }

    public double getCurvature(double t)
    {
        return Math.abs(yPrimet.polynomialSplineDerivative().value(t) /
                xPrimet.polynomialSplineDerivative().value(t))
                / Math.pow(1 + Math.pow(getDerivative(t), 2), 3/2);
    }
}